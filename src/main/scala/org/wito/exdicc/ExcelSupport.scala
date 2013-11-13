package org.wito.exdicc

import java.io.FileInputStream
import java.io.FileOutputStream

import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

trait ExcelSupport {
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

  def removeAllSheetsButLast(wb: Workbook) {
    for (i <- 0 until wb.getNumberOfSheets - 1) {
      wb.removeSheetAt(0)
    }
  }
}