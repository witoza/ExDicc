package org.wito.exdicc

import org.apache.log4j.LogManager
import org.wito.exdicc.ExcelHelper._
import org.apache.poi.ss.usermodel.Workbook

object MultiThreadsProcessor {
  def main(args: Array[String]) {
    val proc = new MultiThreadsProcessor
    proc.process("z:\\exdicc_spanish_2.xls", 10)
  }
}

class MultiThreadsProcessor {

  private val logger = LogManager.getLogger(getClass)

  private def fillWorkbookWithTranslation(wb: Workbook, translation: Map[String, WordInfo]) {

    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      logger.info("Processing sheet " + sheet.getSheetName)
      for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
        val row = sheet.getRow(i)
        val firstCell = row.getCell(0)
        if (!rowIsTranslated(row) && !isCellEmpty(firstCell)) {
          val worldToLookUp = row.getCell(0).getStringCellValue
          val trans = translation.get(worldToLookUp)
          if (trans.isDefined) {
            val wi = trans.get

            if (worldToLookUp != wi.lookedUpWord) {
              createCell(row, 1, wi.lookedUpWord)
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

  def process(fin: String, numOfWorkers: Int = 1) {

    val t1 = System.currentTimeMillis
    val wb = getWorkbook(fin)
    val translation = getTranslatedWords(wb, numOfWorkers)
    logger.info("Translating...");
    fillWorkbookWithTranslation(wb, translation)
    setCellStyleForModifiedRows(wb)
    saveWorkbook(wb, fin)
    logger.info("Done in " + (System.currentTimeMillis - t1) + "ms.")

  }

}