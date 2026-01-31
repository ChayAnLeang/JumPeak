package com.jp.jumpeak.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.jp.jumpeak.databinding.ActivityThemeBinding
import com.jp.jumpeak.presentation.ui.parties.activity.PartiesActivity
import com.jp.jumpeak.util.classes.ThemeUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeActivity : BaseActivity() {
    private val themeUtil by lazy { ThemeUtil(this) }
    private val binding by lazy { ActivityThemeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initDefaultTheme()
        initView()
    }

    private fun initView(){
        binding.apply {
            mtb.setNavigationOnClickListener { finish() }
            mbtSave.setOnClickListener { switchTheme() }
        }
    }

    private fun initDefaultTheme(){
        val mode = themeUtil.getMode()
        binding.apply {
            rbLight.isChecked = mode == AppCompatDelegate.MODE_NIGHT_NO
            rbDark.isChecked = mode == AppCompatDelegate.MODE_NIGHT_YES
            rbFollowSystem.isChecked = mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    private fun switchTheme(){
        val mode = if(binding.rbLight.isChecked) AppCompatDelegate.MODE_NIGHT_NO
        else if(binding.rbDark.isChecked) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        themeUtil.setTheme(mode)
        restartApp()
    }

    private fun restartApp() {
        val intent = Intent(this, PartiesActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}