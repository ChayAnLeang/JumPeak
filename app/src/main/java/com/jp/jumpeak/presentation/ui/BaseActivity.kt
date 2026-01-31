package com.jp.jumpeak.presentation.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.jp.jumpeak.util.classes.LanguageUtil

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageUtil(newBase).applyLanguage())
    }
}