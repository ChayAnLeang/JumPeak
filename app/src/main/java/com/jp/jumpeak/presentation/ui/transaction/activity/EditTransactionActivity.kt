package com.jp.jumpeak.presentation.ui.transaction.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.databinding.ActivityEditTransactionBinding
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.TransactionType
import com.jp.jumpeak.presentation.ui.BaseActivity
import com.jp.jumpeak.presentation.viewmodel.TransactionViewModel
import com.jp.jumpeak.util.classes.DateTimePickerDialogUtil
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.DateTimeUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditTransactionActivity : BaseActivity() {
    private val dialogUtil by lazy { DialogUtil(this) }
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val prefs by lazy { getSharedPreferences("app_prefs",MODE_PRIVATE) }
    private val balance by lazy { prefs.getFloat("balance",0f).toDouble() }
    private val binding by lazy { ActivityEditTransactionBinding.inflate(layoutInflater) }
    private val dateTimePickerDialogUtil by lazy {
        DateTimePickerDialogUtil(supportFragmentManager) { datetime ->
            binding.tietDateTime.setText(DateTimeUtil.format(datetime))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        observeTransactionById()
        observeEditOrDeleteTransaction()
    }

    private fun observeTransactionById(){
        val transactionId = intent.getLongExtra("transaction_id",0L)
        transactionViewModel.getById(transactionId)
        transactionViewModel.transactionById.observe(this) { result ->
            showLoading(true)
            result.onSuccess { transaction ->
                showLoading(false)
                fillForm(transaction)
            }.onFailure { e ->
                Toast.makeText(applicationContext,e.message, Toast.LENGTH_SHORT).show()
                finish()
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

    private fun fillForm(transaction: Transaction){
        handleValidation()
        val transactionType = transaction.type
        binding.apply {
            tietNote.setText(transaction.note)
            mtb.setNavigationOnClickListener { finish() }
            rbDebt.isChecked = transactionType == TransactionType.DEBT
            rbRepayment.isChecked = transactionType == TransactionType.REPAYMENT
            tietAmount.setText(AmountUtil.format(transaction.amount).replace("USD","").trim())
            mbtSave.setOnClickListener { submitTransaction(transaction) }
            mbtDelete.setOnClickListener { handleDeleteTransaction(transaction) }
        }
        binding.tietDateTime.apply {
            setText(DateTimeUtil.format(transaction.datetime))
            setOnClickListener { dateTimePickerDialogUtil.show() }
        }
    }

    private fun handleValidation(){
        val textWatcher = TextWatcherUtil.setup {
            val amount = binding.tietAmount.text.toString()
            val dateTime = binding.tietDateTime.text.toString()
            val isEnabled = amount.isNotEmpty() && amount.toDouble() > 0.0 && dateTime.isNotEmpty()
            binding.mbtSave.apply {
                this.isEnabled = isEnabled
                alpha = if(isEnabled) 1f else 0.5f
            }
        }
        binding.apply {
            tietAmount.addTextChangedListener(textWatcher)
            tietDateTime.addTextChangedListener(textWatcher)
        }
    }

    private fun submitTransaction(transaction: Transaction){
        val note = binding.tietNote.text.toString().trim()
        val dateTime = binding.tietDateTime.text.toString()
        val amount = binding.tietAmount.text.toString().toDouble()
        val transactionType = if(binding.rbDebt.isChecked) TransactionType.DEBT
        else TransactionType.REPAYMENT
        val newTransaction = Transaction(
            transaction.id,
            transaction.partiesId,
            amount,
            note,
            DateTimeUtil.toDate(dateTime).time,
            transactionType
        )
        val currentAmount = transaction.amount
        val currentType = transaction.type
        val (transactionTypeName,newTotalDebt,newTotalRepayment) = getNew(
            transactionType,
            currentType,
            currentAmount,
            amount
        )
        if(newTotalRepayment > newTotalDebt){
            dialogUtil.showError(
                getString(
                    R.string.not_allow_edit_message,
                    transactionTypeName,
                    AmountUtil.format(amount)
                )
            )
            return
        }
        else if(newTotalRepayment == newTotalDebt){
            dialogUtil.showConfirm(getString(R.string.fully_paid_message)) {
                transactionViewModel.manage(Action.EDIT,newTransaction,true)
            }
            return
        }
        transactionViewModel.manage(Action.EDIT,newTransaction,false)
    }

    private fun getNew(
        newType: TransactionType,
        currentType: TransactionType,
        currentAmount: Double,
        newAmount: Double
    ): Triple<String, Double, Double>{
        var totalDebt = prefs.getFloat("total_debt",0f).toDouble()
        var totalRepayment = prefs.getFloat("total_repayment",0f).toDouble()
        val (currentTotalDebt,currentTotalRepayment) = when(currentType){
            TransactionType.DEBT -> Pair(totalDebt - currentAmount,totalRepayment)
            TransactionType.REPAYMENT -> Pair(totalDebt,totalRepayment - currentAmount)
        }
        return when(newType){
            TransactionType.DEBT -> {
                Triple(getString(R.string.debt),currentTotalDebt + newAmount,currentTotalRepayment)
            }
            TransactionType.REPAYMENT -> {
                Triple(getString(R.string.repayment),currentTotalDebt,currentTotalRepayment + newAmount)
            }
        }
    }

    private fun handleDeleteTransaction(transaction: Transaction){
        if(transaction.type == TransactionType.DEBT){
            val currentAmount = transaction.amount
            if(currentAmount > balance){
                dialogUtil.showError(
                    getString(
                        R.string.not_allow_delete_message,
                        AmountUtil.format(currentAmount),
                        AmountUtil.format(balance)
                    )
                )
                return
            }
        }
        dialogUtil.showDelete { transactionViewModel.manage(Action.DELETE,transaction,false) }
    }

    private fun observeEditOrDeleteTransaction(){
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
}