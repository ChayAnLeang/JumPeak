package com.jp.jumpeak.util.objects

import android.content.Context
import android.content.Intent

object RestartAppUtil {
    fun restart(context: Context,targetActivity:Class<*>){
        val intent = Intent(context,targetActivity)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_NEW_TASK
        )
        context.startActivity(intent)
    }
}