package com.jp.jumpeak.presentation.ui.invoice.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.jp.jumpeak.R
import com.jp.jumpeak.databinding.FragmentUnpaidInvoiceListDialogBinding
import com.jp.jumpeak.presentation.ui.invoice.activity.InvoiceDetailActivity
import com.jp.jumpeak.presentation.ui.invoice.adapter.InvoiceAdapter
import com.jp.jumpeak.presentation.viewmodel.InvoiceViewModel
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UnpaidInvoiceListDialogFragment : DialogFragment() {
    private var searchJob: Job?= null
    private var _binding: FragmentUnpaidInvoiceListDialogBinding?= null
    private val binding get() = _binding!!
    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private val navigationUtil by lazy { NavigationUtil(requireContext()) }
    private val invoiceAdapter by lazy {
        InvoiceAdapter(requireContext()){ invoiceId,totalPaid ->
            navigationUtil.navigateTo(
                InvoiceDetailActivity::class.java,
                bundleOf(
                    INVOICE_ID_KEY to invoiceId,
                    TOTAL_PAID_KEY to totalPaid,
                    DISPLAY_SUMMARY_KEY to true
                )
            )
        }
    }

    companion object{
        private const val INVOICE_ID_KEY = "invoice_id"
        private const val TOTAL_PAID_KEY = "total_paid"
        private const val DISPLAY_SUMMARY_KEY = "display_summary"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnpaidInvoiceListDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeUnpaidInvoices()
        observeUnpaidInvoiceCount()
    }

    private fun initView(){
        setupToolbar()
        setupSearchCustomerByName()
        binding.rv.adapter = invoiceAdapter
    }

    private fun setupToolbar(){
        binding.mtb.setOnMenuItemClickListener { items ->
            when(items.itemId){
                R.id.close -> dismiss()
            }
            true
        }
    }

    private fun setupSearchCustomerByName(){
        val textWatcher = TextWatcherUtil.setup {
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(300)
                val name = binding.etSearch.text.toString().trim()
                invoiceViewModel.updateQuery(customerName = name)
            }
        }
        binding.etSearch.addTextChangedListener(textWatcher)
    }

    private fun observeUnpaidInvoices(){
        invoiceViewModel.updateQuery(customerName = "")
        invoiceViewModel.allUnpaidInvoices.observe(viewLifecycleOwner) { pagingData ->
            invoiceAdapter.submitData(lifecycle,pagingData)
        }
        invoiceAdapter.addLoadStateListener { loadState ->
            when(loadState.refresh){
                is LoadState.Loading -> binding.pb.isVisible = true
                is LoadState.NotLoading -> {
                    if(invoiceAdapter.itemCount == 0) showData(false)
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
            tvNoData.isVisible = !isVisible
            icReceipt.isVisible = !isVisible
        }
    }

    private fun observeUnpaidInvoiceCount(){
        invoiceViewModel.numberOfUnpaidInvoices.observe(viewLifecycleOwner){ numberOfUnpaidInvoices ->
            binding.mtb.title = getString(R.string.unpaid_format,numberOfUnpaidInvoices)
        }
    }
}