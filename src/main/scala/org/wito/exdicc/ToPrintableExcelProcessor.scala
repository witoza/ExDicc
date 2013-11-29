package org.wito.exdicc

import org.apache.log4j.LogManager
import org.wito.exdicc.ExcelHelper._

object ToPrintableExcelProcessor {
  def main(args: Array[String]) {
    new ToPrintableExcelProcessor().process(new ProcessingRequest("z:\\exdicc_spanish2.xls", "z:\\exdicc_spanish2_print.xls"))
  }
}

class ToPrintableExcelProcessor {
  private val logger = LogManager.getLogger(getClass)

  def process(preq: ProcessingRequest) {
    val wb = getWorkbook(preq)
    val toPrintSheet = wb.createSheet("ToPrint")

    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      if (sheet ne toPrintSheet) {
        logger.info("Processing sheet " + sheet.getSheetName)
        for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
          val row = sheet.getRow(i)
          val cell0 = row.getCell(0)
          if (rowIsTranslated(row)) {
            if (i > 0)
              copyRowToSheetWithoutCells(row, toPrintSheet, List(1, 3, 4))
          } else {
            logger.info("Skipping row " + cell0 + " as not translated ")
          }
        }
      }
    }
    changeRowHeight(toPrintSheet, 10.0f)
    removeSheetsBut(wb, toPrintSheet)
    saveWorkbook(wb, preq.fout)
  }
}