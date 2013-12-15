package org.wito.exdicc

import org.apache.log4j.LogManager
import org.wito.exdicc.ExcelHelper._

object ToAnyMemo {
  def main(args: Array[String]) {
    val proc = new ToAnyMemo
    proc.process(new ProcessingRequest("z:\\exdicc_spanish.xls", "z:\\exdicc_spanish_anymemo.xls"))
  }
}

class ToAnyMemo {
  private val logger = LogManager.getLogger(getClass)

  def process(preq: ProcessingRequest) {
    val wb = getWorkbook(preq)
    val amSheet = wb.createSheet("AnyMemo")

    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      if (sheet ne amSheet) {
        logger.info("Processing sheet " + sheet.getSheetName)
        for (i <- 1 to sheet.getLastRowNum) {
          val row = sheet.getRow(i)
          if (rowIsTranslated(row)) {
            if (isCellEmpty(row, 1)) {
              createCell(row, 1, row.getCell(0).getStringCellValue)
            }
            if (isCellEmpty(row, 3)) {
              createCell(row, 3, sheet.getSheetName)
            }
            copyRowToSheetWithoutCells(row, amSheet, List(0))
          } else {
            logger.info("Skipping row #" + i + " as not translated")
          }
        }
      }
    }
    removeSheetsBut(wb, amSheet)
    swapColumnsAtSheet(amSheet, 0, 1)
    saveWorkbook(wb, preq.fout)
  }
}