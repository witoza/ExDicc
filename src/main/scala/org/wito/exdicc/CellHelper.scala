package org.wito.exdicc

import java.io.FileOutputStream
import java.net.SocketTimeoutException

import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook

object CellHelper {

  private val logger = LogManager.getLogger(getClass)

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