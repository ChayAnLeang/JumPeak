package com.jp.jumpeak.util.classes

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class ThemeUtil(context: Context) {
    private val key = "theme_mode"
    private val prefs = context.getSharedPreferences("app_prefs",Context.MODE_PRIVATE)

    fun applyTheme() {
        val themeMode = prefs.getInt(key, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    fun setTheme(mode: Int) {
        prefs.edit { putInt(key, mode) }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getMode(): Int {
        return prefs.getInt(key, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}