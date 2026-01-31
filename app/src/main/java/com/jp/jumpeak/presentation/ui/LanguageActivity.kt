package com.jp.jumpeak.presentation.ui

import android.content.Intent
import android.os.Bundle
import com.jp.jumpeak.databinding.ActivityLanguageBinding
import com.jp.jumpeak.presentation.ui.parties.activity.PartiesActivity
import com.jp.jumpeak.util.classes.LanguageUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : BaseActivity() {
    private val languageUtil by lazy { LanguageUtil(this) }
    private val binding by lazy { ActivityLanguageBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDefaultLanguage()
        initView()
    }

    private fun initView(){
        binding.apply {
            mtb.setNavigationOnClickListener { finish() }
            mbtSave.setOnClickListener { switchLanguage() }
        }
    }

    private fun initDefaultLanguage(){
        val lang = languageUtil.getLanguage()
        binding.apply {
            rbKM.isChecked = lang == "km"
            rbEN.isChecked = lang == "en"
        }
    }

    private fun switchLanguage(){
        val lang = if(binding.rbKM.isChecked) "km"
        else "en"
        languageUtil.setLanguage(lang)
        restartApp()
    }

    private fun restartApp() {
        val intent = Intent(this, PartiesActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
        finish()
    }
}