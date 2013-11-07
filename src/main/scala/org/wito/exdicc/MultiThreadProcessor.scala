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

object MultiThreadsProcessor {
  def main(args: Array[String]) {
    val proc = new MultiThreadsProcessor()
    proc.process(new ProcessingRequest("z:\\exdicc_sample.xlsx", "z:\\exdicc_sample2.xlsx"), 4)
  }
}

class MultiThreadsProcessor {

  private val logger = LogManager.getLogger(getClass())

  private def fillWorkbookWithTranslation(wb: Workbook, translation: Map[String, WordInfo]) {

    val style = wb.createCellStyle()
    style.setFillForegroundColor(IndexedColors.YELLOW.getIndex())
    style.setFillPattern(CellStyle.BIG_SPOTS)

    for (i <- 0 to wb.getNumberOfSheets() - 1) {
      val sheet = wb.getSheetAt(i)
      for (i <- 1 to sheet.getLastRowNum()) {
        val row = sheet.getRow(i)
        val cell = row.getCell(0)
        val worldToBeLookedUp = cell.getStringCellValue()

        val wi = translation.get(worldToBeLookedUp).get

        val ncell1 = row.createCell(1)
        ncell1.setCellType(Cell.CELL_TYPE_STRING)
        ncell1.setCellValue(wi.lookedUpWord)

        if (worldToBeLookedUp != wi.lookedUpWord && wi.lookedUpWord != "") {
          ncell1.setCellStyle(style)
        }

        val ncell2 = row.createCell(2)
        ncell2.setCellType(Cell.CELL_TYPE_STRING)
        ncell2.setCellValue(wi.quickDef)

        val ncell3 = row.createCell(3)
        ncell3.setCellType(Cell.CELL_TYPE_STRING)
        ncell3.setCellValue(wi.quickPos)
      }
    }
  }

  private def getTranslatedWords(wb: Workbook, numOfWorkers: Int): Map[String, WordInfo] = {
    val wordTranslator = new MultiThreadWordTranslator(numOfWorkers)
    return wordTranslator.translateWorldsFromWorkbook(wb)
  }

  def saveWorkbook(wb: Workbook, preq: ProcessingRequest) {
    val fileOut = new FileOutputStream(preq.fout)
    try {
      wb.write(fileOut)
    } finally {
      fileOut.close
    }
  }

  def getWorkbook(preq: ProcessingRequest): Workbook = {
    return WorkbookFactory.create(new FileInputStream(preq.fin))
  }

  def process(preq: ProcessingRequest, numOfWorkers: Int) {

    val t1 = System.currentTimeMillis;
    val wb = getWorkbook(preq)
    val translation = getTranslatedWords(wb, numOfWorkers)
    fillWorkbookWithTranslation(wb, translation)
    saveWorkbook(wb, preq)
    logger.info("Done in " + (System.currentTimeMillis - t1) + "ms.")

  }

}