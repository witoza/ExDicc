package org.wito.exdicc

import java.net.SocketTimeoutException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

import scala.concurrent.Lock

import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.Workbook

class MultiThreadWordTranslator(numOfWorkers: Int) {

  private val logger = LogManager.getLogger(getClass)

  private val wordsToHarvest = new LinkedBlockingQueue[String]()

  private val wordsInfo = scala.collection.mutable.Map[String, WordInfo]()

  private val wordsInfoLock = new Lock()

  private var workerPool: ExecutorService = _

  private class Worker extends Runnable {

    private val spanishDict = new SpanishDict

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

    def retryWhenSocketTimeout(times: Int, body: => Unit) {
      var retry = true
      var retryNbr = 0
      while (retry) {
        retry = false
        try {
          body
        } catch {
          case e: SocketTimeoutException =>
            logger.warn("Problem with internet connection", e)
            if (retryNbr < times) {
              retry = true
              retryNbr += 1
            }
          case e: Exception =>
            logger.warn("Generic exception", e)
        }
      }
    }

    def run() {
      while (true) {
        val word = wordsToHarvest.poll(1, TimeUnit.SECONDS)
        if (word == null && workerPool.isShutdown) {
          logger.info("No more work, closing")
          return
        } else {
          retryWhenSocketTimeout(3, {
            emit(spanishDict.getQuickDefinition(word))
          })
        }
      }
    }
  }

  private def preparePool() {
    workerPool = Executors.newFixedThreadPool(numOfWorkers)
    for (i <- 0 until numOfWorkers) {
      workerPool.submit(new Worker)
    }
  }

  private def closePool() {
    workerPool.shutdown
    workerPool.awaitTermination(10, TimeUnit.DAYS)
  }

  private def extractWords(wb: Workbook) {
    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      for (i <- 0 to sheet.getLastRowNum) {
        val row = sheet.getRow(i)
        if (row.getCell(0) != null && row.getCell(1) == null) {
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