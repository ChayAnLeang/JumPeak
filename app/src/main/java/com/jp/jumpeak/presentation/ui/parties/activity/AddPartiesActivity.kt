package com.jp.jumpeak.presentation.ui.parties.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Parties
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.databinding.ActivityAddPartiesBinding
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.PartiesType
import com.jp.jumpeak.enums.TransactionType
import com.jp.jumpeak.presentation.ui.BaseActivity
import com.jp.jumpeak.presentation.viewmodel.PartiesViewModel
import com.jp.jumpeak.util.classes.DateTimePickerDialogUtil
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.objects.DateTimeUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AddPartiesActivity : BaseActivity() {
    private val dialogUtil by lazy { DialogUtil(this) }
    private val partiesViewModel: PartiesViewModel by viewModels()
    private val binding by lazy { ActivityAddPartiesBinding.inflate(layoutInflater) }
    private val dateTimePickerDialogUtil by lazy {
        DateTimePickerDialogUtil(supportFragmentManager){ datetime ->
            binding.tietDateTime.setText(DateTimeUtil.format(datetime))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        observeAddParties()
    }

    private fun initView(){
        setupToolbar()
        handleValidation()
        binding.apply {
            mbtSave.setOnClickListener { submitParties() }
            tietDateTime.setOnClickListener { dateTimePickerDialogUtil.show() }
        }
    }

    private fun setupToolbar(){
        val title = intent.getStringExtra("title")
        binding.mtb.apply {
            this.title = title
            setNavigationOnClickListener { finish() }
        }
        binding.rbDebtor.apply {
            isChecked = title == getString(R.string.add_person_who_owe_me)
            setOnClickListener { binding.mtb.title = getString(R.string.add_person_who_owe_me) }
        }
        binding.rbCreditor.apply {
            isChecked = title == getString(R.string.add_person_i_owe)
            setOnClickListener { binding.mtb.title = getString(R.string.add_person_i_owe) }
        }
    }

    private fun handleValidation(){
        val textWatcher = TextWatcherUtil.setup {
            val name = binding.tietName.text.toString().trim()
            val dateTime = binding.tietDateTime.text.toString()
            val debtAmount = binding.tietDebtAmount.text.toString()
            val phoneNumber = binding.tietPhoneNumber.text.toString()
            val isEnabled = name.isNotEmpty() && dateTime.isNotEmpty() && debtAmount.isNotEmpty() &&
                    debtAmount.toDouble() > 0.0 && phoneNumber.isNotEmpty()
            binding.mbtSave.apply {
                this.isEnabled = isEnabled
                alpha = if(isEnabled) 1f else 0.5f
            }
        }
        binding.apply {
            tietName.addTextChangedListener(textWatcher)
            tietDateTime.addTextChangedListener(textWatcher)
            tietDebtAmount.addTextChangedListener(textWatcher)
            tietPhoneNumber.addTextChangedListener(textWatcher)
        }
    }

    private fun submitParties(){
        val partiesId = UUID.randomUUID().toString()
        val name = binding.tietName.text.toString().trim()
        val note = binding.tietNote.text.toString().trim()
        val dateTime = binding.tietDateTime.text.toString()
        val phoneNumber = binding.tietPhoneNumber.text.toString()
        val debtAmount = binding.tietDebtAmount.text.toString().toDouble()
        val partiesType = if(binding.rbDebtor.isChecked) PartiesType.OWE_ME
        else PartiesType.I_OWE
        val newParties = Parties(partiesId,name,phoneNumber,partiesType)
        val firstTransaction = Transaction(
            partiesId = partiesId,
            amount = debtAmount,
            note = note,
            datetime = DateTimeUtil.toDate(dateTime).time,
            type = TransactionType.DEBT
        )
        partiesViewModel.manage(Action.ADD,newParties,firstTransaction)
    }

    private fun observeAddParties(){
        partiesViewModel.manage.observe(this) { result ->
            showLoading(true)
            result.onSuccess { message ->
                Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { e ->
                showFailure(e.message)
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