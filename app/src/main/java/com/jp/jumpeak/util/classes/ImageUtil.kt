package com.jp.jumpeak.util.classes

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.createBitmap

class ImageUtil(val context: Context) {
    fun send(view: View,invoiceNo:String){
        val bitmap = getBitmapFromView(view)
        val file = saveBitmap(bitmap,invoiceNo)
        val uri = FileProvider.getUriForFile(context,"com.jp.jumpeak.provider",file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM,uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent,"Send Invoice"))
    }

    private fun getBitmapFromView(view:View):Bitmap{
        val bitmap = createBitmap(view.width, view.height)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveBitmap(bitmap: Bitmap,invoiceNo:String):File{
        val file = File(context.cacheDir,"invoice_$invoiceNo.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100,outputStream)
        outputStream.apply {
            flush()
            close()
        }
        return file
    }
}