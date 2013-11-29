package org.wito.exdicc

import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import org.wito.exdicc.ExcelHelper._

object MultiThreadsProcessor {
  def main(args: Array[String]) {
    val proc = new MultiThreadsProcessor
    val preq = new ProcessingRequest("z:\\exdicc_spanish2.xls")
    proc.process(preq, 10)
  }
}

class MultiThreadsProcessor {

  private val logger = LogManager.getLogger(getClass)

  private def fillWorkbookWithTranslation(wb: Workbook, translation: Map[String, WordInfo]) {

    val changeInOriginalWordCellStyle = wb.createCellStyle
    changeInOriginalWordCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex)
    changeInOriginalWordCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND)

    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      logger.info("Processing sheet " + sheet.getSheetName)
      for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
        val row = sheet.getRow(i)
        if (!rowIsTranslated(row)) {
          val worldToLookUp = row.getCell(0).getStringCellValue
          val trans = translation.get(worldToLookUp)
          if (trans.isDefined) {
            val wi = trans.get

            if (worldToLookUp != wi.lookedUpWord && wi.lookedUpWord != "") {
              val ncell1 = createCell(row, 1, wi.lookedUpWord)
              ncell1.setCellStyle(changeInOriginalWordCellStyle)
            }

            createCell(row, 2, wi.quickDef)
            createCell(row, 4, wi.quickPos)
          }
        }
      }
    }
  }

  private def getTranslatedWords(wb: Workbook, numOfWorkers: Int): Map[String, WordInfo] = {
    val wordTranslator = new MultiThreadWordTranslator(numOfWorkers)
    return wordTranslator.translateWorldsFromWorkbook(wb)
  }

  def process(preq: ProcessingRequest, numOfWorkers: Int = 1) {

    val t1 = System.currentTimeMillis
    val wb = getWorkbook(preq)
    val translation = getTranslatedWords(wb, numOfWorkers)
    logger.info("Translating...");
    fillWorkbookWithTranslation(wb, translation)
    saveWorkbook(wb, preq.fout)
    logger.info("Done in " + (System.currentTimeMillis - t1) + "ms.")

  }

}