package org.wito.exdicc

import org.apache.log4j.LogManager

object ToPrintableExcelProcessor {
  def main(args: Array[String]) {

    new ToPrintableExcelProcessor().process(new ProcessingRequest("z:\\exdicc_spanish1.xlsx", "z:\\exdicc_spanish1_print.xlsx"))

  }
}

class ToPrintableExcelProcessor extends ExcelSupport {
  private val logger = LogManager.getLogger(getClass)

  def process(preq: ProcessingRequest) {
    val wb = getWorkbook(preq)
    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      logger.info("Processing sheet " + sheet.getSheetName)
      for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
        val row = sheet.getRow(i)
        val cell0 = row.getCell(0)
        if (CellHelper.isCellEmpty(row, 1)) {
          CellHelper.createCell(row, 1, cell0.getStringCellValue)
        }
      }
    }
    saveWorkbook(wb, preq.fout)
  }
}