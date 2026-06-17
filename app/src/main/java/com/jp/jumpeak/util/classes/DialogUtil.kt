package com.jp.jumpeak.util.classes

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.jp.jumpeak.R

class DialogUtil(val context: Context){
    private val dialog = AlertDialog.Builder(context).setCancelable(false)
    fun showDelete(block:() -> Unit) {
        dialog
            .setTitle(context.getString(R.string.delete))
            .setMessage(context.getString(R.string.delete_message))
            .setNegativeButton(context.getString(R.string.no)){ dialog, _ -> dialog.dismiss() }
            .setPositiveButton(context.getString(R.string.yes)) { _, _ -> block() }
            .show()
    }

    fun showMessage(message: String?,isErrorMode: Boolean = true) {
        if(isErrorMode) dialog.setTitle(context.getString(R.string.error))
        dialog
            .setMessage(message ?: "Unknown Error")
            .setPositiveButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}