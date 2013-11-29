package org.wito.misc

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import org.wito.exdicc.ProcessingRequest
import org.wito.exdicc.ExcelHelper._

object MakeJournal {
  def main(args: Array[String]) {
    val wb = getWorkbook(new ProcessingRequest("w:\\asia_kalendar.xlsx", "w:\\asia_journal.txt"))

    val writer = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(new File("w:\\asialog.txt")), "UTF-8"));

    for (i <- 0 until wb.getNumberOfSheets) {
      val sheet = wb.getSheetAt(i)
      for (i <- 1 to sheet.getLastRowNum) {
        val row = sheet.getRow(i)
        val title = row.getCell(0).getStringCellValue
        val start = row.getCell(1).getStringCellValue
        val end = row.getCell(2).getStringCellValue
        val description = if (isCellEmpty(row, 4)) "" else row.getCell(4).getStringCellValue

        val formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        val startDate = formatter.parse(start);
        val startDateStr = new SimpleDateFormat("EE, MM/dd/yyyy").format(startDate)

        writer.write("~~~~~~~~~~~~~~~~~~~~~~~\n")
        writer.write(startDateStr + ", " + title + "\n\n")
        writer.write(description + " \n")
      }
    }

    writer.close

  }
}