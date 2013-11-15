package org.wito.exdicc

import java.io.FileInputStream

import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.wito.exdicc.CellHelper._
import org.wito.exdicc.CellHelper.createCell
import org.wito.exdicc.CellHelper.isCellEmpty

object ToAnyMemoFormatProcessor {
  def main(args: Array[String]) {
    new ToAnyMemoFormatProcessor().process(new ProcessingRequest("z:\\exdicc_spanish1.xlsx", "z:\\exdicc_spanish1_anymemo.xlsx"))
  }
}

class ToAnyMemoFormatProcessor extends ExcelSupport {
  private val logger = LogManager.getLogger(getClass)

  private def copyRowToSheet(row: Row, sheet: Sheet) {
    val newRow = sheet.createRow(sheet.getLastRowNum + 1)
    for (j <- 1 to row.getLastCellNum) { // all but first
      copyCell(newRow, j - 1, row.getCell(j))
    }
  }

  private def swapColumnsAtSheet(sheet: Sheet, c1: Int, c2: Int) {

    for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
      val row = sheet.getRow(i)
      val cell1 = row.getCell(c1)
      val cell1Style = cell1.getCellStyle
      val cell1Value = cell1.getStringCellValue

      val cell2 = row.getCell(c2)
      copyCell(row, c1, cell2)
      cell2.setCellStyle(cell1Style)
      cell2.setCellValue(cell1Value)
    }
  }

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
              copyRowToSheet(row, amSheet)
          } else {
            logger.info("Skipping row " + cell0 + " as not translated ")
          }
        }
      }
    }
    removeAllSheetsButLast(wb)
    swapColumnsAtSheet(amSheet, 0, 1)
    saveWorkbook(wb, preq.fout)
  }
}