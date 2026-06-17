package com.jp.jumpeak.presentation.ui.payment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Payment
import com.jp.jumpeak.databinding.FragmentAddPaymentDialogBinding
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.presentation.viewmodel.PaymentViewModel
import com.jp.jumpeak.util.objects.DatePickerDialogUtil
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.classes.NumberFormatUtil
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.PopupMenuUtil
import com.jp.jumpeak.util.objects.DateUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class AddPaymentDialogFragment : DialogFragment() {
    private var newAmount: Double = 0.0
    private val paymentViewModel:PaymentViewModel by viewModels()
    private var _binding: FragmentAddPaymentDialogBinding?= null
    private val binding get() = _binding!!
    private val dialogUtil by lazy { DialogUtil(requireContext()) }
    private val balance by lazy { arguments?.getDouble(BALANCE_KEY) ?: 0.0 }
    private val invoiceId by lazy { arguments?.getLong(INVOICE_ID_KEY) ?: 0 }
    private val currencySymbol by lazy { arguments?.getString(CURRENCY_SYMBOL_KEY) ?: "" }
    private val popupMenu by lazy {
        PopupMenuUtil.setup(requireContext(),binding.etPaymentMethod,R.menu.payment_method_menu) { listener ->
            binding.etPaymentMethod.setText(listener.title)
        }
    }
    private val datePickerDialogUtil by lazy {
        DatePickerDialogUtil.setup{ selectedDate ->
            binding.etPaymentDate.setText(DateUtil.format(selectedDate))
        }
    }

    companion object {
        private const val BALANCE_KEY = "balance"
        private const val INVOICE_ID_KEY = "invoice_id"
        private const val NEW_AMOUNT_KEY = "new_amount"
        private const val DISPLAY_RESULT_KEY = "display_result"
        private const val DISPLAY_SUMMARY_KEY = "display_summary"
        private const val CURRENCY_SYMBOL_KEY = "currency_symbol"
        private const val PAID_AMOUNT_RESULT_KEY = "paid_amount_result"
        fun newInstance(invoiceId: Long, balance: Double, currencySymbol: String): AddPaymentDialogFragment {
            return AddPaymentDialogFragment().apply {
                arguments = bundleOf(
                    BALANCE_KEY to balance,
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
        _binding = FragmentAddPaymentDialogBinding.inflate(inflater,container,false)
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
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeManagePayment()
    }

    private fun initView(){
        setupToolbar()
        setupValidation()
        val calendar = Calendar.getInstance().time.time
        binding.apply {
            etPaymentMethod.setOnClickListener { popupMenu.show() }
            etPaid.addTextChangedListener(NumberFormatUtil(binding.etPaid))
            btSave.setOnClickListener { submitPayment() }
            tvBalance.text = AmountUtil.format(balance,currencySymbol)
            tvAmountDue.text = AmountUtil.format(balance,currencySymbol)
        }
        binding.etPaymentDate.apply {
            setText(DateUtil.format(calendar))
            setOnClickListener { datePickerDialogUtil.show(parentFragmentManager,"Date Picker") }
        }
    }

    private fun setupToolbar(){
        binding.mtb.setOnMenuItemClickListener { items ->
            when(items.itemId){
                R.id.close -> dismiss()
            }
            true
        }
    }

    private fun setupValidation(){
        val textWatcher = TextWatcherUtil.setup {
            val paid = binding.etPaid.text.toString()
            val paymentDate = binding.etPaymentDate.text.toString()
            val paymentMethod = binding.etPaymentMethod.text.toString()
            val isEnabled = paymentDate.isNotEmpty() && paymentMethod.isNotEmpty() && paid.isNotEmpty()
            binding.btSave.apply {
                this.isEnabled = isEnabled
                alpha = if(isEnabled) 1f else 0.5f
            }
            val balance = balance - (paid.replace(",","").trim().toDoubleOrNull() ?: 0.0)
            binding.tvBalance.text = AmountUtil.format(balance,currencySymbol)
        }
        binding.apply {
            etPaid.addTextChangedListener(textWatcher)
            etPaymentDate.addTextChangedListener(textWatcher)
            etPaymentMethod.addTextChangedListener(textWatcher)
        }
    }

    private fun submitPayment(){
        val paymentDate = binding.etPaymentDate.text.toString()
        val paymentMethod = binding.etPaymentMethod.text.toString()
        val paid = binding.etPaid.text.toString().replace(",","").trim().toDouble()
        newAmount = paid
        val payment = Payment(
            invoiceId = invoiceId,
            amount = paid,
            paymentMethod = paymentMethod,
            date = DateUtil.toDate(paymentDate)?.time!!
        )
        paymentViewModel.manage(payment, Action.ADD)
    }

    private fun observeManagePayment() {
        paymentViewModel.message.observe(viewLifecycleOwner){ result ->
            showLoading(true)
            result.onSuccess { message ->
                showLoading(false)
                setFragmentResult()
                showToast(message)
            }.onFailure { e ->
                showLoading(false)
                dialogUtil.showMessage(e.message)
            }
        }
    }

    private fun showLoading(isVisible:Boolean){
        binding.apply {
            pb.isVisible = isVisible
            btSave.isEnabled = !isVisible
        }
    }

    private fun setFragmentResult(){
        parentFragmentManager.apply {
            setFragmentResult(
                PAID_AMOUNT_RESULT_KEY,
                bundleOf(NEW_AMOUNT_KEY to newAmount)
            )
            setFragmentResult(
                DISPLAY_RESULT_KEY,
                bundleOf(DISPLAY_SUMMARY_KEY to true)
            )
        }
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
        dismiss()
    }
}