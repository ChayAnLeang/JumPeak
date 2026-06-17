package com.jp.jumpeak.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.jp.jumpeak.databinding.ActivityBusinessProfileBinding

class BusinessProfileActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("app_prefs", MODE_PRIVATE) }
    private val binding by lazy { ActivityBusinessProfileBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView(){
        initBusinessProfile()

        binding.apply {
            mtb.setNavigationOnClickListener { finish() }
            btSave.setOnClickListener { submitBusinessProfile() }
        }
    }

    private fun initBusinessProfile(){
        val businessName = prefs.getString("business_name","")
        val primaryPhoneNumber = prefs.getString("primary_phone_number","")
        val secondaryPhoneNumber = prefs.getString("secondary_phone_number","")
        val tertiaryPhoneNumber = prefs.getString("tertiary_phone_number","")
        val address = prefs.getString("address","")
        binding.apply {
            etAddress.setText(address)
            etBusinessName.setText(businessName)
            etPrimaryPhoneNumber.setText(primaryPhoneNumber)
            etSecondaryPhoneNumber.setText(secondaryPhoneNumber)
            etTertiaryPhoneNumber.setText(tertiaryPhoneNumber)
        }
    }

    private fun submitBusinessProfile(){
        val address = binding.etAddress.text.toString().trim()
        val businessName = binding.etBusinessName.text.toString().trim()
        val primaryPhoneNumber = binding.etPrimaryPhoneNumber.text.toString()
        val tertiaryPhoneNumber = binding.etTertiaryPhoneNumber.text.toString()
        val secondaryPhoneNumber = binding.etSecondaryPhoneNumber.text.toString()
        prefs.edit {
            putString("address",address)
            putString("business_name",businessName)
            putString("primary_phone_number",primaryPhoneNumber)
            putString("tertiary_phone_number",tertiaryPhoneNumber)
            putString("secondary_phone_number",secondaryPhoneNumber)
        }
        finish()
    }
}