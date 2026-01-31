package com.jp.jumpeak.util.classes

import android.content.Context
import android.content.Intent
import android.os.Bundle

class NavigationUtil(val context: Context) {
    fun navigateTo(targetActivity: Class<*>,extras: Bundle ?= null){
        val intent = Intent(context,targetActivity)
        extras?.let { intent.putExtras(extras) }
        context.startActivity(intent)
    }
}