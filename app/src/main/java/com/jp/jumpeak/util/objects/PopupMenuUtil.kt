package com.jp.jumpeak.util.objects

import android.content.Context
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu

object PopupMenuUtil{
    fun setup(context: Context, view: View, menu: Int, onItemClick:(MenuItem) -> Unit): PopupMenu {
        val popupMenu = PopupMenu(context, view)
        popupMenu.gravity = Gravity.END
        popupMenu.menuInflater.inflate(menu,popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { listener ->
            onItemClick(listener)
            true
        }
        return popupMenu
    }
}