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
import com.jp.jumpeak.databinding.FragmentEditPaymentDialogBinding
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

@AndroidEntryPoint
class EditPaymentDialogFragment : DialogFragment() {
    private var preAmount: Double = 0.0
    private var newAmount: Double = 0.0
    private val paymentViewModel:PaymentViewModel by viewModels()
    private var _binding: FragmentEditPaymentDialogBinding?= null
    private val binding get() = _binding!!
    private val dialogUtil by lazy { DialogUtil(requireContext()) }
    private val paymentId by lazy { arguments?.getLong(PAYMENT_ID_KEY,0) ?: 0 }
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
        private const val PRE_AMOUNT_KEY = "pre_amount"
        private const val NEW_AMOUNT_KEY = "new_amount"
        private const val PAYMENT_ID_KEY = "payment_id"
        private const val PAID_AMOUNT_RESULT_KEY = "paid_amount_result"
        fun newInstance(paymentId: Long): EditPaymentDialogFragment {
            return EditPaymentDialogFragment().apply {
                arguments = bundleOf(PAYMENT_ID_KEY to paymentId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPaymentDialogBinding.inflate(inflater,container,false)
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
        observePaymentById()
        observeManagePayment()
    }

    private fun observePaymentById(){
        paymentViewModel.getById(paymentId)
        paymentViewModel.obj.observe(viewLifecycleOwner) { result ->
            showLoading(true)
            result.onSuccess { payment ->
                showLoading(false)
                initView()
                fillForm(payment)
            }.onFailure { e ->
                showToast(e.message)
            }
        }
    }

    private fun initView(){
        setupToolbar()
        setupValidation()
        binding.apply {
            etPaymentMethod.setOnClickListener { popupMenu.show() }
            etPaid.addTextChangedListener(NumberFormatUtil(binding.etPaid))
            etPaymentDate.setOnClickListener { datePickerDialogUtil.show(parentFragmentManager,"Date Picker") }
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
        }
        binding.apply {
            etPaid.addTextChangedListener(textWatcher)
            etPaymentDate.addTextChangedListener(textWatcher)
            etPaymentMethod.addTextChangedListener(textWatcher)
        }
    }

    private fun fillForm(payment: Payment){
        preAmount = payment.amount
        binding.apply {
            etPaymentDate.setText(DateUtil.format(payment.date))
            etPaid.setText(AmountUtil.format(payment.amount).trim())
            etPaymentMethod.setText(payment.paymentMethod)
            btSave.setOnClickListener { submitPayment(payment) }
            btDelete.setOnClickListener {
                dialogUtil.showDelete { paymentViewModel.manage(payment, Action.DELETE) }
            }
        }
    }

    private fun submitPayment(payment: Payment){
        val paymentDate = binding.etPaymentDate.text.toString()
        val paymentMethod = binding.etPaymentMethod.text.toString()
        val paid = binding.etPaid.text.toString().replace(",","").trim().toDouble()
        newAmount = paid
        paymentViewModel.manage(
            payment.copy(
                amount = paid,
                paymentMethod = paymentMethod,
                date = DateUtil.toDate(paymentDate)?.time!!
            ),
            Action.EDIT
        )
    }

    private fun observeManagePayment() {
        paymentViewModel.message.observe(viewLifecycleOwner){ result ->
            showLoading(true)
            result.onSuccess { message ->
                showLoading(false)
                showToast(message)
                parentFragmentManager.setFragmentResult(
                    PAID_AMOUNT_RESULT_KEY,
                    bundleOf(
                        PRE_AMOUNT_KEY to preAmount,
                        NEW_AMOUNT_KEY to newAmount
                    )
                )
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

    private fun showToast(message: String?){
        Toast.makeText(requireContext(),message, Toast.LENGTH_SHORT).show()
        dismiss()
    }
}