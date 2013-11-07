package org.wito.exdicc

import java.io.FileInputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import scala.collection.mutable.ListBuffer
import scala.concurrent.Lock
import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.util.concurrent.TimeUnit
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService

class MultiThreadWordTranslator(numOfWorkers: Int) {

  private val logger = LogManager.getLogger(getClass())

  private val wordsToHarvest = new LinkedBlockingQueue[String]()

  private val wordsInfo = scala.collection.mutable.Map[String, WordInfo]()

  private val wordsInfoLock = new Lock()

  private var workerPool: ExecutorService = _

  private class Worker extends Runnable {

    private def emit(wi: WordInfo) {
      wordsInfoLock.acquire()
      try {
        wordsInfo += (wi.originalWord -> wi)
      } finally {
        wordsInfoLock.release();
      }
    }

    def run() {
      while (true) {
        val word = wordsToHarvest.poll(1, TimeUnit.SECONDS)
        if (word == null && workerPool.isShutdown()) {
          logger.info("No more work - closing")
          return
        } else {
          emit(SpanishDict(word))
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
    workerPool.shutdown()
    workerPool.awaitTermination(10, TimeUnit.DAYS)
  }

  private def extractWords(wb: Workbook) {
    for (i <- 0 to wb.getNumberOfSheets() - 1) {
      val sheet = wb.getSheetAt(i)
      for (i <- 1 to sheet.getLastRowNum()) {
        val theWord = sheet.getRow(i).getCell(0).getStringCellValue()
        wordsToHarvest.add(theWord);
      }
    }
  }

  def translateWorldsFromWorkbook(wb: Workbook): Map[String, WordInfo] = {
    preparePool()
    try {
      extractWords(wb);
    } finally {
      closePool()
    }
    return wordsInfo.toMap
  }
}