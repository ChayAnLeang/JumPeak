package com.jp.jumpeak.util.objects

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object WindowInsertsListenerUtil {
    fun setup(view: View){
        ViewCompat.setOnApplyWindowInsetsListener(view) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(view.paddingLeft,view.paddingTop,view.paddingRight,systemBars.bottom)
            insets
        }
    }
}