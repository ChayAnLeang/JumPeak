package com.jp.jumpeak.presentation.ui.transaction.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.jp.jumpeak.R
import com.jp.jumpeak.data.dto.Summary
import com.jp.jumpeak.databinding.ActivityTransactionBinding
import com.jp.jumpeak.presentation.ui.BaseActivity
import com.jp.jumpeak.presentation.ui.transaction.TransactionAdapter
import com.jp.jumpeak.presentation.viewmodel.TransactionViewModel
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.TabLayoutUtil
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.edit
import com.jp.jumpeak.presentation.ui.ExportTransactionBottomSheetFragment
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.objects.WindowInsertsListenerUtil

@AndroidEntryPoint
class TransactionActivity : BaseActivity() {
    private val dialogUtil by lazy { DialogUtil(this) }
    private val navigationUtil by lazy { NavigationUtil(this) }
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val partiesId by lazy { intent.getStringExtra("parties_id") ?: "" }
    private val binding by lazy { ActivityTransactionBinding.inflate(layoutInflater) }
    private val transactionAdapter by lazy {
        TransactionAdapter { isSettled,transactionId,amount ->
            if(isSettled){
                dialogUtil.showError(getString(R.string.not_allow_edit_or_delete_message))
            }
            else{
                navigationUtil.navigateTo(
                    EditTransactionActivity::class.java,
                    bundleOf("amount" to amount,"transaction_id" to transactionId)
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        observeSummaryByPartiesId()
        observeTransactionByPartiesId()
    }

    private fun initView(){
        setupToolbar()
        handleTabLayout()
        WindowInsertsListenerUtil.setup(binding.main)
        binding.apply {
            rv.adapter = transactionAdapter
            tvAddDebt.setOnClickListener {
                navigateToAddTransactionActivity(getString(R.string.add_debt))
            }
            tvAddRepayment.setOnClickListener {
                navigateToAddTransactionActivity(getString(R.string.add_repayment))
            }
        }
    }

    private fun setupToolbar(){
        val partiesName = intent.getStringExtra("parties_name") ?: ""
        val phoneNumber = intent.getStringExtra("phone_number") ?: ""
        binding.mtb.apply {
            title = partiesName
            subtitle = phoneNumber
            setNavigationOnClickListener { finish() }
            setOnMenuItemClickListener { items ->
                if(items.itemId == R.id.export){
                    val exportTransactionBottomSheet = ExportTransactionBottomSheetFragment.newInstance(
                        partiesId,
                        partiesName
                    )
                    exportTransactionBottomSheet.show(supportFragmentManager,exportTransactionBottomSheet.tag)
                }
                true
            }
        }
    }

    private fun handleTabLayout(){
        val partiesId = intent.getStringExtra("parties_id") ?: ""
        transactionViewModel.updateQuery(partiesId,false)
        TabLayoutUtil.setup(binding.tl) { position ->
            when(position){
                0 -> transactionViewModel.updateQuery(partiesId,false)
                1 -> transactionViewModel.updateQuery(partiesId,true)
            }
        }
    }

    private fun navigateToAddTransactionActivity(title: String){
        navigationUtil.navigateTo(AddTransactionActivity::class.java, bundleOf("title" to title))
    }

    private fun observeSummaryByPartiesId(){
        transactionViewModel.summaryByPartiesId.observe(this) { summary ->
            showLoading(true)
            showSummary(summary)
        }
    }

    private fun showLoading(isVisible: Boolean){
        binding.apply {
            pbTotalDebt.isVisible = isVisible
            pbTotalRepayment.isVisible = isVisible
            pbBalance.isVisible = isVisible
        }
    }

    private fun showSummary(summary: Summary){
        showLoading(false)
        val totalDebt = summary.totalDebt
        val totalRepayment = summary.totalRepayment
        val balance = totalDebt - totalRepayment
        binding.apply {
            tvTotalDebt.text = AmountUtil.format(totalDebt)
            tvTotalRepayment.text = AmountUtil.format(totalRepayment)
            tvBalance.text = AmountUtil.format(balance)
        }
        getSharedPreferences("app_prefs",MODE_PRIVATE).edit {
            putFloat("total_debt",totalDebt.toFloat())
            putFloat("total_repayment",totalRepayment.toFloat())
            putFloat("balance",balance.toFloat())
        }
    }

    private fun observeTransactionByPartiesId(){
        transactionViewModel.transactionByPartiesId.observe(this) { transactions ->
            transactionAdapter.submitData(lifecycle,transactions)
        }
        transactionAdapter.addLoadStateListener { loadState ->
            when(loadState.refresh){
                is LoadState.Loading -> binding.pb.isVisible = true
                is LoadState.NotLoading -> {
                    if(transactionAdapter.itemCount == 0) showData(false)
                    else showData(true)
                }
                is LoadState.Error -> showData(false)
            }
        }
    }

    private fun showData(isVisible: Boolean){
        binding.apply {
            pb.isVisible = false
            rv.isVisible = isVisible
            icList.isVisible = !isVisible
            tvNoData.isVisible = !isVisible
        }
    }
}