package com.jp.jumpeak

import android.app.Application
import com.jp.jumpeak.util.classes.LanguageUtil
import com.jp.jumpeak.util.classes.ThemeUtil
import com.jp.jumpeak.util.objects.NotificationUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JumPeakApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ThemeUtil(this).applyTheme()
        LanguageUtil(this).applyLanguage()
        NotificationUtil.createChannel(this)
    }
}