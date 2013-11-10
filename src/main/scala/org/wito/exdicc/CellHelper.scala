package org.wito.exdicc

import java.io.FileOutputStream

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook

object CellHelper {

  def isCellEmpty(row: Row, cellNbr: Int): Boolean =
    isCellEmpty(row.getCell(cellNbr))

  def isCellEmpty(cell: Cell): Boolean =
    cell == null || cell.getStringCellValue.trim.isEmpty

  def rowIsTranslated(row: Row): Boolean =
    !isCellEmpty(row, 0) && !isCellEmpty(row, 1)

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

  def saveWorkbook(wb: Workbook, fout: String) {
    val fileOut = new FileOutputStream(fout)
    try {
      wb.write(fileOut)
    } finally {
      fileOut.close
    }
  }

}