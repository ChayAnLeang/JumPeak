package com.jp.jumpeak.util.classes

import android.content.Context
import androidx.core.content.edit
import java.util.Locale

class LanguageUtil(private val context: Context) {
    private val key = "language"
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setLanguage(langCode: String) {
        prefs.edit { putString(key, langCode) }
    }

    fun getLanguage(): String {
        return prefs.getString(key, "km") ?: "km"
    }

    fun applyLanguage(): Context {
        return updateResources(getLanguage())
    }

    private fun updateResources(lang: String): Context {
        val locale = Locale.forLanguageTag(lang)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.apply {
            setLocale(locale)
            setLayoutDirection(locale)
        }
        return context.createConfigurationContext(config)
    }
}