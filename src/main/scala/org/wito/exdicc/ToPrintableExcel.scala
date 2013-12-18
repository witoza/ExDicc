package org.wito.exdicc

import org.apache.log4j.LogManager
import org.wito.exdicc.ExcelHelper._

object ToPrintableExcel {
  def main(args: Array[String]) {
    val proc = new ToPrintableExcel
    proc.process(new ProcessingRequest("z:\\exdicc_spanish_3.xls", "z:\\exdicc_spanish_3_print.xls"))
  }
}

class ToPrintableExcel {
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
          if (rowIsTranslated(row)) {
            if (i > 0) {
              val isTranslated = isCellEmpty(row, 1)
              val row1 = copyRowToSheetWithoutCells(row, toPrintSheet, List(if (isTranslated) 1 else 0, 3, 4))
              removeCellStyles(row1)
            }
          } else {
            logger.info("Skipping row #" + i + " as not translated")
          }
        }
        logger.info("Done")
      }
    }
    setRowsHeightInPoints(toPrintSheet, 10.0f)
    removeSheetsBut(wb, toPrintSheet)
    saveWorkbook(wb, preq.fout)
  }
}