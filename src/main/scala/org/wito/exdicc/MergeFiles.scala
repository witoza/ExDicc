package org.wito.exdicc

import org.wito.exdicc.ExcelHelper._

object MergeFiles {
  def main(args: Array[String]) {
    val merger = new MergeFiles
    merger.merge("z:\\exdicc_spanish1.xls", "z:\\exdicc_spanish2.xls", "z:\\exdicc_spanish.xls")
  }
}

class MergeFiles {

  def merge(fin1: String, fin2: String, fout: String) {
    val wb1 = getWorkbook(fin1)
    val wb2 = getWorkbook(fin2)

    for (i <- 0 until wb1.getNumberOfSheets) {
      val sheet1 = wb1.getSheetAt(i)
      val sheet2 = wb2.getSheet(sheet1.getSheetName)
      for (i <- 1 to sheet2.getLastRowNum) {
        copyRowToSheet(sheet2.getRow(i), sheet1)
      }
    }
    setCellStyleForModifiedRows(wb1)
    saveWorkbook(wb1, fout)
  }

}