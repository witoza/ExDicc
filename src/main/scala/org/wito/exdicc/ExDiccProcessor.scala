package org.wito.exdicc

import java.io.FileInputStream
import java.io.FileOutputStream
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFColor
import java.awt.Color
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.log4j.LogManager
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Row

case class ProcessingRequest(fin: String, fout: String)

class ExDiccProcessor {

  private val logger = LogManager.getLogger(getClass())

  private def processRow(row: Row, style: CellStyle) {
    val cell = row.getCell(0)
    val worldToBeLookedUp = cell.getStringCellValue()
    logger.info("Looking up '" + worldToBeLookedUp + "' ... ")
    val wi = SpanishDict(worldToBeLookedUp)
    logger.info("Retrived " + wi.toString)

    val ncell1 = row.createCell(1)
    ncell1.setCellType(Cell.CELL_TYPE_STRING)
    ncell1.setCellValue(wi.lookedUpWord)

    if (worldToBeLookedUp != wi.lookedUpWord && wi.lookedUpWord != "") {
      ncell1.setCellStyle(style)
    }

    val ncell2 = row.createCell(2)
    ncell2.setCellType(Cell.CELL_TYPE_STRING)
    ncell2.setCellValue(wi.quickDef)

    val ncell3 = row.createCell(3)
    ncell3.setCellType(Cell.CELL_TYPE_STRING)
    ncell3.setCellValue(wi.quickPos)
  }

  private def processSheet(sheet: Sheet, style: CellStyle) {
    logger.info(">>> Processing sheet '" + sheet.getSheetName() + "'")
    for (i <- 1 to sheet.getLastRowNum()) {
      processRow(sheet.getRow(i), style)
    }
  }

  def process(preq: ProcessingRequest) {

    val inp = new FileInputStream(preq.fin)
    val wb = WorkbookFactory.create(inp)

    val thereIsAChangeStyle = wb.createCellStyle()
    thereIsAChangeStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex())
    thereIsAChangeStyle.setFillPattern(CellStyle.BIG_SPOTS)

    for (i <- 0 to wb.getNumberOfSheets() - 1) {
      processSheet(wb.getSheetAt(i), thereIsAChangeStyle)
    }

    val fileOut = new FileOutputStream(preq.fout)
    try {
      wb.write(fileOut)
    } finally {
      fileOut.close()
    }

  }
}