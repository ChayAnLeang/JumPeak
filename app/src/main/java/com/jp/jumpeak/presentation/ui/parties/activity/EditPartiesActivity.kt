package com.jp.jumpeak.presentation.ui.parties.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Parties
import com.jp.jumpeak.databinding.ActivityEditPartiesBinding
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.PartiesType
import com.jp.jumpeak.presentation.ui.BaseActivity
import com.jp.jumpeak.presentation.viewmodel.PartiesViewModel
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPartiesActivity : BaseActivity() {
    private val dialogUtil by lazy { DialogUtil(this) }
    private val partiesViewModel: PartiesViewModel by viewModels()
    private val binding by lazy { ActivityEditPartiesBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observePartiesById()
        observeEditOrDeleteParties()
    }

    private fun observePartiesById(){
        val partiesId = intent.getStringExtra("parties_id") ?: ""
        partiesViewModel.getById(partiesId)
        partiesViewModel.partiesById.observe(this) { result ->
            showLoading(true)
            result.onSuccess { parties ->
                showLoading(false)
                fillForm(parties)
            }.onFailure { e ->
                showMessage(e.message)
            }
        }
    }

    private fun showLoading(isVisible: Boolean){
        binding.apply {
            pb.isVisible = isVisible
            mbtSave.isEnabled = !isVisible
            div.alpha = if(isVisible) 0.5f else 1f
        }
    }

    private fun fillForm(parties: Parties){
        handleValidation()
        val partiesType = parties.type
        binding.apply {
            tietName.setText(parties.name)
            tietPhoneNumber.setText(parties.phoneNumber)
            mtb.setNavigationOnClickListener { finish() }
            rbDebtor.isChecked = partiesType == PartiesType.OWE_ME
            rbCreditor.isChecked = partiesType == PartiesType.I_OWE
            mbtSave.setOnClickListener { submitParties(parties) }
            mbtDelete.setOnClickListener {
                dialogUtil.showDelete {
                    partiesViewModel.manage(Action.DELETE,parties)
                }
            }
        }
    }

    private fun handleValidation(){
        val textWatcher = TextWatcherUtil.setup {
            val name = binding.tietName.text.toString().trim()
            val phoneNumber = binding.tietPhoneNumber.text.toString()
            val isEnabled = name.isNotEmpty() && phoneNumber.isNotEmpty()
            binding.mbtSave.apply {
                this.isEnabled = isEnabled
                alpha = if(isEnabled) 1f else 0.5f
            }
        }
        binding.apply {
            tietName.addTextChangedListener(textWatcher)
            tietPhoneNumber.addTextChangedListener(textWatcher)
        }
    }

    private fun submitParties(parties: Parties){
        val partiesName = binding.tietName.text.toString().trim()
        val phoneNumber = binding.tietPhoneNumber.text.toString()
        val partiesType = if(binding.rbDebtor.isChecked) PartiesType.OWE_ME
        else PartiesType.I_OWE
        val newParties = Parties(parties.id,partiesName,phoneNumber,partiesType)
        partiesViewModel.manage(Action.EDIT,newParties)
    }

    private fun observeEditOrDeleteParties(){
        partiesViewModel.manage.observe(this) { result ->
            showLoading(true)
            result.onSuccess { message ->
                showMessage(message)
            }.onFailure { e ->
                showFailure(e.message)
            }
        }
    }

    private fun showMessage(message: String?){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showFailure(message: String?){
        showLoading(false)
        if(message?.contains("UNIQUE") == true){
            val phoneNumber = binding.tietPhoneNumber.text.toString()
            dialogUtil.showError(getString(R.string.phone_number_already_exist_message,phoneNumber))
            return
        }
        dialogUtil.showError(message)
    }
}