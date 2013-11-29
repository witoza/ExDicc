package org.wito.exdicc

import org.apache.log4j.LogManager
import org.wito.exdicc.ExcelHelper._

object ToAnyMemoFormatProcessor {
  def main(args: Array[String]) {
    new ToAnyMemoFormatProcessor().process(new ProcessingRequest("z:\\exdicc_spanish2.xls", "z:\\exdicc_spanish2_anymemo.xls"))
  }
}

class ToAnyMemoFormatProcessor {
  private val logger = LogManager.getLogger(getClass)

  def process(preq: ProcessingRequest) {
    val wb = getWorkbook(preq)
    val amSheet = wb.createSheet("AnyMemo")

    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      if (sheet ne amSheet) {
        logger.info("Processing sheet " + sheet.getSheetName)
        for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
          val row = sheet.getRow(i)
          val cell0 = row.getCell(0)
          if (rowIsTranslated(row)) {
            if (isCellEmpty(row, 1)) {
              createCell(row, 1, cell0.getStringCellValue)
            }
            if (isCellEmpty(row, 3)) {
              createCell(row, 3, sheet.getSheetName)
            }
            if (i > 0)
              copyRowToSheetWithoutCells(row, amSheet, List(1))
          } else {
            logger.info("Skipping row " + cell0 + " as not translated ")
          }
        }
      }
    }
    removeSheetsBut(wb, amSheet)
    swapColumnsAtSheet(amSheet, 0, 1)
    saveWorkbook(wb, preq.fout)
  }
}