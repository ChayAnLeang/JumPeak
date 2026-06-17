package com.jp.jumpeak.presentation.ui.payment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.jp.jumpeak.R
import com.jp.jumpeak.databinding.FragmentPaymentHistoryDialogBinding
import com.jp.jumpeak.presentation.ui.payment.PaymentAdapter
import com.jp.jumpeak.presentation.viewmodel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentHistoryDialogFragment : DialogFragment() {
    private val paymentViewModel:PaymentViewModel by viewModels()
    private var _binding: FragmentPaymentHistoryDialogBinding?= null
    private val binding get() = _binding!!
    private val invoiceId by lazy { arguments?.getLong(INVOICE_ID_KEY,0) ?: 0 }
    private val currencySymbol by lazy { arguments?.getString(CURRENCY_SYMBOL_KEY,"") ?: "" }
    private val paymentAdapter by lazy {
        PaymentAdapter(requireContext()){ paymentId ->
            showEditPaymentDialog(paymentId)
        }
    }

    private fun showEditPaymentDialog(paymentId: Long){
        val editPaymentDialogFragment = EditPaymentDialogFragment.newInstance(paymentId)
        editPaymentDialogFragment.show(parentFragmentManager,editPaymentDialogFragment.tag)
    }

    companion object {
        private const val INVOICE_ID_KEY = "invoice_id"
        private const val CURRENCY_SYMBOL_KEY = "currency_symbol"
        fun newInstance(invoiceId:Long, currencySymbol:String): PaymentHistoryDialogFragment {
            return PaymentHistoryDialogFragment().apply {
                arguments = bundleOf(
                    INVOICE_ID_KEY to invoiceId,
                    CURRENCY_SYMBOL_KEY to currencySymbol
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentHistoryDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            setCancelable(false)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observePaymentsByInvoiceId()
    }

    private fun initView(){
        setupToolbar()
        binding.rv.adapter = paymentAdapter
        paymentViewModel.setInvoiceId(invoiceId)
    }

    private fun setupToolbar(){
        binding.mtb.setOnMenuItemClickListener { items ->
            when(items.itemId){
                R.id.close -> dismiss()
            }
            true
        }
    }

    private fun observePaymentsByInvoiceId(){
        paymentViewModel.paymentsByInvoiceId.observe(viewLifecycleOwner){ payments ->
            binding.pb.isVisible = true
            if(payments.isEmpty()){
                showData(false)
            }
            else{
                paymentAdapter.submitList(payments)
                paymentAdapter.setCurrencySymbol(currencySymbol)
                showData(true)
            }
        }
    }

    private fun showData(isVisible: Boolean){
        binding.apply {
            pb.isVisible = false
            rv.isVisible = isVisible
            tvNoData.isVisible = !isVisible
            icPayment.isVisible = !isVisible
        }
    }
}