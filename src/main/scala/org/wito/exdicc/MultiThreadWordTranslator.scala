package org.wito.exdicc

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import scala.concurrent.Lock
import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.Workbook
import java.net.SocketTimeoutException

class MultiThreadWordTranslator(numOfWorkers: Int) {

  private val logger = LogManager.getLogger(getClass)

  private val wordsToHarvest = new LinkedBlockingQueue[String]()

  private val wordsInfo = scala.collection.mutable.Map[String, WordInfo]()

  private val wordsInfoLock = new Lock()

  private var workerPool: ExecutorService = _

  private class Worker extends Runnable {

    private def emit(wi: Option[WordInfo]) {
      if (wi.isEmpty) {
        return
      }
      wordsInfoLock.acquire
      try {
        wordsInfo += (wi.get.originalWord -> wi.get)
      } finally {
        wordsInfoLock.release
      }
    }

    def run() {
      while (true) {
        val word = wordsToHarvest.poll(1, TimeUnit.SECONDS)
        if (word == null && workerPool.isShutdown) {
          logger.info("No more work, closing")
          return
        } else {
          var retry = true
          var retryNbr = 0
          while (retry) {
            retry = false
            try {
              emit(SpanishDict(word))
            } catch {
              case e: SocketTimeoutException =>
                logger.warn("Check internet connection, can't translate word: " + word, e)
                if (retryNbr < 3) {
                  retry = true
                  retryNbr += 1
                }
              case e: Exception =>
                logger.warn("Problem with translating: " + word, e)
            }
          }
        }
      }
    }
  }

  private def preparePool() {
    workerPool = Executors.newFixedThreadPool(numOfWorkers)
    for (i <- 0 to numOfWorkers - 1) {
      workerPool.submit(new Worker())
    }
  }

  private def closePool() {
    workerPool.shutdown
    workerPool.awaitTermination(10, TimeUnit.DAYS)
  }

  private def extractWords(wb: Workbook) {
    for (i <- 0 to wb.getNumberOfSheets - 1) {
      val sheet = wb.getSheetAt(i)
      for (i <- 1 to sheet.getLastRowNum) {
        val row = sheet.getRow(i)
        if (row.getCell(1) == null) {
          val theWord = row.getCell(0).getStringCellValue
          wordsToHarvest.add(theWord)
        }
      }
    }
  }

  def translateWorldsFromWorkbook(wb: Workbook): Map[String, WordInfo] = {
    preparePool
    try {
      extractWords(wb)
    } finally {
      closePool
    }
    return wordsInfo.toMap
  }
}