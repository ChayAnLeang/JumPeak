package com.jp.jumpeak.presentation.ui.invoice.fragment

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.jp.jumpeak.R
import com.jp.jumpeak.databinding.FragmentInvoiceBinding
import com.jp.jumpeak.presentation.ui.invoice.activity.InvoiceDetailActivity
import com.jp.jumpeak.presentation.ui.invoice.activity.ManageInvoiceActivity
import com.jp.jumpeak.presentation.ui.invoice.adapter.InvoiceAdapter
import com.jp.jumpeak.presentation.viewmodel.InvoiceViewModel
import com.jp.jumpeak.util.objects.DatePickerDialogUtil
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.DateUtil
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.activityViewModels
import com.jp.jumpeak.presentation.viewmodel.CustomerNameShareViewModel

@AndroidEntryPoint
class InvoiceFragment : Fragment() {
    private val calendar = Calendar.getInstance()
    private var _binding: FragmentInvoiceBinding?= null
    private val binding get() = _binding!!
    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private val navigationUtil by lazy { NavigationUtil(requireContext()) }
    private val customerNameShareViewModel: CustomerNameShareViewModel by activityViewModels()
    private val datePickerDialogUtil by lazy {
        DatePickerDialogUtil.setup{ selectedDate ->
            binding.tvDate.text = DateUtil.format(selectedDate)
            invoiceViewModel.updateQuery(startDate = selectedDate, endDate = selectedDate)
        }
    }
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
        _binding = FragmentInvoiceBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeCustomerName()
        observeDailyInvoices()
        observeUnpaidInvoiceCount()
    }

    private fun initView(){
        setupCalender()
        binding.apply {
            rv.adapter = invoiceAdapter
            icNext.setOnClickListener { updateDate(1) }
            icPrevious.setOnClickListener { updateDate(-1) }
            tvDate.setOnClickListener { datePickerDialogUtil.show(parentFragmentManager,"Date Picker") }
            btAddInvoice.setOnClickListener {
                navigationUtil.navigateTo(
                    ManageInvoiceActivity::class.java,
                    bundleOf(DISPLAY_SUMMARY_KEY to false)
                )
            }
            btUnpaid.setOnClickListener {
                val unpaidInvoiceListDialogFragment = UnpaidInvoiceListDialogFragment()
                unpaidInvoiceListDialogFragment.show(parentFragmentManager,unpaidInvoiceListDialogFragment.tag)
            }
        }
        updateDate(0)
    }

    private fun setupCalender(){
        calendar.apply {
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.HOUR_OF_DAY,0)
        }
    }

    private fun updateDate(amount: Int){
        calendar.add(Calendar.DATE,amount)
        val startDate = calendar.timeInMillis
        binding.tvDate.text = DateUtil.format(startDate)
        calendar.add(Calendar.DATE,1)
        val endDate = calendar.timeInMillis
        invoiceViewModel.updateQuery(startDate = startDate,endDate = endDate)
        calendar.add(Calendar.DATE,-1)
    }

    private fun observeCustomerName(){
        customerNameShareViewModel.name.observe(viewLifecycleOwner) { name ->
            invoiceViewModel.updateQuery(customerName = name)
        }
    }

    private fun observeDailyInvoices(){
        invoiceViewModel.dailyInvoices.observe(viewLifecycleOwner) { pagingData ->
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
            binding.btUnpaid.text = getString(R.string.unpaid_format,numberOfUnpaidInvoices)
        }
    }
}