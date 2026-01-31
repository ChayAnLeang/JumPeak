package com.jp.jumpeak.util.classes

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.jp.jumpeak.util.objects.DateTimeUtil
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableSheet
import java.io.File
import java.io.OutputStream

class ExportUtil(val context: Context, val headers: List<String>) {
    private val now = DateTimeUtil.format(System.currentTimeMillis())
    fun excel(sheetName: String,onInsertCell:(WritableSheet) -> Unit) {
        val fileName = String.format("%s %s.xls",sheetName,now.substringBefore(",").trim().replace("/","-"))
        val outputStream = getOutputStream(fileName)
        outputStream.use { os ->
            val workbook = Workbook.createWorkbook(os)
            val sheet = workbook.createSheet(sheetName,0)
            headers.forEachIndexed { index, title -> sheet.addCell(Label(index, 0, title)) }
            onInsertCell(sheet)
            workbook.apply {
                write()
                close()
            }
        }
    }

    private fun getOutputStream(fileName: String): OutputStream {
        val resolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE,"application/vnd.ms-excel")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(uri)
            }?: throw Exception("Fail To Export")
        } else {
            // Android 9 or Lower
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            file.outputStream()
        }
    }
}