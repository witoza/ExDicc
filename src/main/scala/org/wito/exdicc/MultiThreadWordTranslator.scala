package org.wito.exdicc

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.Workbook
import org.wito.exdicc.ExcelHelper.retryWhenSocketTimeout
import org.wito.exdicc.ExcelHelper.rowIsTranslated

class MultiThreadWordTranslator(numOfWorkers: Int) {

  private val logger = LogManager.getLogger(getClass)

  private val wordsToHarvest = new LinkedBlockingQueue[String]()

  private val wordsInfo = scala.collection.concurrent.TrieMap[String, WordInfo]()

  private var workerPool: ExecutorService = _

  private class Worker extends Runnable {

    private val spanishDict = new SpanishDict

    def run() {
      var close = false
      while (!close) {
        val word = wordsToHarvest.poll(1, TimeUnit.SECONDS)
        if (word == null && workerPool.isShutdown) {
          close = true
        } else {
          retryWhenSocketTimeout(3) {
            val wi = spanishDict.getQuickDefinition(word)
            if (wi.isDefined) {
              wordsInfo.putIfAbsent(wi.get.originalWord, wi.get)
            }
          }

        }
      }
      logger.info("No more work, closing")
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
      for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
        val row = sheet.getRow(i)
        if (!rowIsTranslated(row)) {
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