package org.wito.exdicc

import java.io.FileInputStream

import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.wito.exdicc.CellHelper.copyCell
import org.wito.exdicc.CellHelper.createCell
import org.wito.exdicc.CellHelper.isCellEmpty
import org.wito.exdicc.CellHelper.saveWorkbook

object ToAnyMemoFormatProcessor {
  def main(args: Array[String]) {
    new ToAnyMemoFormatProcessor().process(new ProcessingRequest("z:\\exdicc_spanish1.xlsx", "z:\\exdicc_spanish1_anymemo.xlsx"))
  }
}

class ToAnyMemoFormatProcessor {
  private val logger = LogManager.getLogger(getClass)

  private def copyRowToSheet(row: Row, sheet: Sheet) {
    val newRow = sheet.createRow(sheet.getLastRowNum + 1)
    for (j <- 1 to row.getLastCellNum) { // all but first
      copyCell(newRow, j - 1, row.getCell(j))
    }
  }

  private def swapColumns(sheet: Sheet, c1: Int, c2: Int) {

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

  private def removeAllSheetsButLast(wb: Workbook) {
    for (i <- 0 until wb.getNumberOfSheets - 1) {
      wb.removeSheetAt(0)
    }
  }

  def process(preq: ProcessingRequest) {
    val wb = WorkbookFactory.create(new FileInputStream(preq.fin))
    val amSheet = wb.createSheet("AnyMemo")

    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      if (sheet ne amSheet) {
        logger.info("Processing sheet " + sheet.getSheetName)
        for (i <- 0 to sheet.getLastRowNum) {
          val row = sheet.getRow(i)
          val cell0 = row.getCell(0)
          if (isCellEmpty(row, 1)) {
            createCell(row, 1, cell0.getStringCellValue)
          }
          if (isCellEmpty(row, 3)) {
            createCell(row, 3, sheet.getSheetName)
          }
          if (i > 0)
            copyRowToSheet(row, amSheet)
        }
      }
    }
    removeAllSheetsButLast(wb)
    swapColumns(amSheet, 0, 1)
    saveWorkbook(wb, preq.fout);
  }
}