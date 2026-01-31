package com.jp.jumpeak.presentation.ui.transaction.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.databinding.ActivityAddTransactionBinding
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.TransactionType
import com.jp.jumpeak.presentation.ui.BaseActivity
import com.jp.jumpeak.presentation.ui.parties.PartiesListDialogFragment
import com.jp.jumpeak.presentation.viewmodel.TransactionViewModel
import com.jp.jumpeak.util.classes.DateTimePickerDialogUtil
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.DateTimeUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTransactionActivity : BaseActivity() {
    private var balance = 0.0
    private var partiesId = ""
    private val dialogUtil by lazy { DialogUtil(this) }
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val binding by lazy { ActivityAddTransactionBinding.inflate(layoutInflater) }
    private val dateTimePickerDialogUtil by lazy {
        DateTimePickerDialogUtil(supportFragmentManager) { datetime ->
            binding.tietDateTime.setText(DateTimeUtil.format(datetime))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        handleFragmentResult()
        initView()
        observeAddTransaction()
    }

    private fun handleFragmentResult(){
        supportFragmentManager.setFragmentResultListener("parties",this) { _,bundle ->
            balance = bundle.getDouble("balance")
            partiesId = bundle.getString("parties_id") ?: ""
            val partiesName = bundle.getString("parties_name")
            binding.tietSelect.setText(partiesName)
        }
    }

    private fun initView(){
        setupToolbar()
        handleValidation()
        binding.apply {
            mtb.setNavigationOnClickListener { finish() }
            mbtSave.setOnClickListener { submitTransaction() }
            tietDateTime.setOnClickListener { dateTimePickerDialogUtil.show() }
            tietSelect.setOnClickListener {
                val partiesListDialog = PartiesListDialogFragment()
                partiesListDialog.show(supportFragmentManager,partiesListDialog.tag)
            }
        }
    }

    private fun setupToolbar(){
        val title = intent.getStringExtra("title")
        binding.mtb.apply {
            this.title = title
            setNavigationOnClickListener { finish() }
        }
        binding.rbDebt.apply {
            isChecked = title == getString(R.string.add_debt)
            setOnClickListener { binding.mtb.title = getString(R.string.add_debt) }
        }
        binding.rbRepayment.apply {
            isChecked = title == getString(R.string.add_repayment)
            setOnClickListener { binding.mtb.title = getString(R.string.add_repayment) }
        }
    }

    private fun handleValidation(){
        val textWatcher = TextWatcherUtil.setup {
            val people = binding.tietSelect.text.toString()
            val amount = binding.tietAmount.text.toString()
            val dateTime = binding.tietDateTime.text.toString()
            val isEnabled = people.isNotEmpty() && amount.isNotEmpty() && amount.toDouble() > 0.0 &&
                            dateTime.isNotEmpty()
            binding.mbtSave.apply {
                this.isEnabled = isEnabled
                alpha = if(isEnabled) 1f else 0.5f
            }
        }
        binding.apply {
            tietSelect.addTextChangedListener(textWatcher)
            tietAmount.addTextChangedListener(textWatcher)
            tietDateTime.addTextChangedListener(textWatcher)
        }
    }

    private fun submitTransaction(){
        val note = binding.tietNote.text.toString().trim()
        val dateTime = binding.tietDateTime.text.toString()
        val amount = binding.tietAmount.text.toString().toDouble()
        val transactionType = if(binding.rbDebt.isChecked) TransactionType.DEBT
        else TransactionType.REPAYMENT
        val newTransaction = Transaction(
            partiesId = partiesId,
            amount = amount,
            note = note,
            datetime = DateTimeUtil.toDate(dateTime).time,
            type = transactionType
        )
        if(transactionType == TransactionType.REPAYMENT){
            if(amount > balance){
                dialogUtil.showError(
                    getString(
                        R.string.invalid_balance_message,
                        AmountUtil.format(amount),
                        AmountUtil.format(balance)
                    )
                )
                return
            }
            else if(amount == balance){
                dialogUtil.showConfirm(getString(R.string.fully_paid_message)) {
                    transactionViewModel.manage(Action.ADD,newTransaction,true)
                }
                return
            }
        }
        transactionViewModel.manage(Action.ADD,newTransaction,false)
    }

    private fun observeAddTransaction(){
        transactionViewModel.manage.observe(this) { result ->
            showLoading(true)
            result.onSuccess { message ->
                Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure { e ->
                showLoading(false)
                dialogUtil.showError(e.message)
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
}