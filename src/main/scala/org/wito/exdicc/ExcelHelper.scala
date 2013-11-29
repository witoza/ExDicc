package org.wito.exdicc

import java.io.FileOutputStream
import java.net.SocketTimeoutException
import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileInputStream

object ExcelHelper {

  private val logger = LogManager.getLogger(getClass)

  def getWorkbook(preq: ProcessingRequest): Workbook = {
    return WorkbookFactory.create(new FileInputStream(preq.fin))
  }

  def saveWorkbook(wb: Workbook, fout: String) {
    val fileOut = new FileOutputStream(fout)
    try {
      wb.write(fileOut)
    } finally {
      fileOut.close
    }
  }

  def removeSheetsBut(wb: Workbook, sheet: Sheet) {
    for (i <- 0 until wb.getNumberOfSheets) {
      if (wb.getSheetAt(i) ne sheet) {
        wb.removeSheetAt(i)
        return removeSheetsBut(wb, sheet)
      }
    }
  }

  def changeRowHeight(sheet: Sheet, rowHeight: Float) {
    for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
      val row = sheet.getRow(i)
      row.setHeightInPoints(rowHeight)
    }
  }

  def swapColumnsAtSheet(sheet: Sheet, column1: Int, column2: Int) {

    for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
      val row = sheet.getRow(i)
      val cell1 = row.getCell(column1)
      val cell1Style = cell1.getCellStyle
      val cell1Value = cell1.getStringCellValue

      val cell2 = row.getCell(column2)
      copyCell(row, column1, cell2)
      cell2.setCellStyle(cell1Style)
      cell2.setCellValue(cell1Value)
    }
  }

  def retryWhenSocketTimeout(times: Int = 3)(body: => Unit) {
    var retry = true
    var retryNbr = 0
    while (retry) {
      retry = false
      try {
        body
      } catch {
        case e: SocketTimeoutException =>
          logger.info("Recoverable exception " + e.getMessage() + ", retrying " + retryNbr + "/" + times)
          if (retryNbr < times) {
            retry = true
            retryNbr += 1
            logger.info("Too many retries, error", e)
          }
        case e: Exception =>
          logger.warn("Unrecoverable exception", e)
      }
    }
  }

  def isCellEmpty(row: Row, cellNbr: Int): Boolean =
    isCellEmpty(row.getCell(cellNbr))

  def copyRowToSheetWithoutCells(row: Row, sheet: Sheet, cellsToSkip: List[Int] = List[Int]()) {
    val newRow = sheet.createRow(sheet.getLastRowNum)
    var colN = 0
    for (j <- 0 to row.getLastCellNum) {
      if (!cellsToSkip.contains(j)) {
        copyCell(newRow, colN, row.getCell(j))
        colN += 1
      }
    }
    sheet.createRow(sheet.getLastRowNum + 1)
  }

  def isCellEmpty(cell: Cell): Boolean =
    cell == null || cell.getStringCellValue.trim.isEmpty

  def rowIsTranslated(row: Row): Boolean =
    !isCellEmpty(row, 0) && !isCellEmpty(row, 2)

  def createCell(row: Row, cellNbr: Int, value: String): Cell = {
    val cell = row.createCell(cellNbr)
    cell.setCellType(Cell.CELL_TYPE_STRING)
    cell.setCellValue(value)
    cell
  }

  def copyCell(row: Row, cellNbr: Int, ocell: Cell): Cell = {
    val cell = row.createCell(cellNbr)
    cell.setCellType(Cell.CELL_TYPE_STRING)
    if (isCellEmpty(ocell)) {
      cell.setCellValue("")
    } else {
      cell.setCellValue(ocell.getStringCellValue)
      cell.setCellStyle(ocell.getCellStyle)
    }
    cell
  }

}