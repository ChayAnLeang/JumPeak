package com.jp.jumpeak

import android.app.Application
import com.jp.jumpeak.util.classes.ThemeUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KotBungApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeUtil(this).applyTheme()
    }
}