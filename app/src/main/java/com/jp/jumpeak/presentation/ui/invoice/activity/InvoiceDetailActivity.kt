package com.jp.jumpeak.presentation.ui.invoice.activity

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Invoice
import com.jp.jumpeak.data.pojo.InvoicePojo
import com.jp.jumpeak.databinding.ActivityInvoiceDetailBinding
import com.jp.jumpeak.enums.Currency
import com.jp.jumpeak.presentation.ui.invoice.adapter.InvoiceDetailAdapter
import com.jp.jumpeak.presentation.ui.payment.fragment.AddPaymentDialogFragment
import com.jp.jumpeak.presentation.ui.payment.fragment.PaymentHistoryDialogFragment
import com.jp.jumpeak.presentation.viewmodel.InvoiceViewModel
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.classes.ImageUtil
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.DateUtil
import com.jp.jumpeak.util.objects.WindowInsertsListenerUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlin.text.ifEmpty

@AndroidEntryPoint
class InvoiceDetailActivity : AppCompatActivity() {
    private var displaySummary = true
    private var totalDue: Double = 0.0
    private var totalPaid: Double = 0.0
    private var currencySymbol = Currency.KHR.symbol
    private val imageUtil by lazy { ImageUtil(this) }
    private val dialogUtil by lazy { DialogUtil(this) }
    private val navigationUtil by lazy { NavigationUtil(this) }
    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private val invoiceDetailAdapter by lazy { InvoiceDetailAdapter() }
    private val invoiceId by lazy { intent.getLongExtra(INVOICE_ID_KEY,0) }
    private val prefs by lazy { getSharedPreferences("app_prefs",MODE_PRIVATE) }
    private val binding by lazy { ActivityInvoiceDetailBinding.inflate(layoutInflater) }

    companion object{
        private const val FORM_ADD_KEY = "form_add"
        private const val INVOICE_ID_KEY = "invoice_id"
        private const val TOTAL_PAID_KEY = "total_paid"
        private const val PRE_AMOUNT_KEY = "pre_amount"
        private const val NEW_AMOUNT_KEY = "new_amount"
        private const val DISPLAY_RESULT = "display_result"
        private const val DISPLAY_SUMMARY_KEY = "display_summary"
        private const val PAID_AMOUNT_RESULT = "paid_amount_result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getFragmentResult()
        observeInvoiceById()
        observeDeleteInvoice()
        WindowInsertsListenerUtil.setup(binding.main)
    }

    private fun getFragmentResult(){
        totalPaid = intent.getDoubleExtra(TOTAL_PAID_KEY,0.0)
        displaySummary = intent.getBooleanExtra(DISPLAY_SUMMARY_KEY,true)
        supportFragmentManager.apply {
            setFragmentResultListener(PAID_AMOUNT_RESULT,this@InvoiceDetailActivity) { _,bundle ->
                val preAmount = bundle.getDouble(PRE_AMOUNT_KEY)
                val newAmount = bundle.getDouble(NEW_AMOUNT_KEY)
                totalPaid = totalPaid - preAmount + newAmount
                updatePaymentSummary()
            }
            setFragmentResultListener(DISPLAY_RESULT,this@InvoiceDetailActivity) { _,bundle ->
                displaySummary = bundle.getBoolean(DISPLAY_SUMMARY_KEY)
                updatePaymentSummary()
            }
        }
    }

    private fun observeInvoiceById(){
        invoiceViewModel.getById(invoiceId)
        invoiceViewModel.obj.observe(this){ result ->
            showLoading(true)
            result.onSuccess { invoicePojo ->
                showLoading(false)
                showInvoiceItem(invoicePojo)
            }.onFailure { e ->
                showToast(e.message)
            }
        }
    }

    private fun showLoading(isVisible:Boolean){
        binding.apply {
            pb.isVisible = isVisible
            btSend.isEnabled = !isVisible
            btPayment.isEnabled = !isVisible
            invoiceView.alpha = if(isVisible) 0.5f else 1f
        }
    }

    private fun showInvoiceItem(invoicePojo: InvoicePojo){
        showBusinessProfile()
        val invoice = invoicePojo.invoice
        initView(invoice)
        val items = invoicePojo.items
        val customer = invoicePojo.customer
        totalDue = invoice.totalDue
        val subTotal = items.sumOf { item -> item.amount }
        currencySymbol = invoice.currency.symbol
        invoiceDetailAdapter.setCurrencySymbol(currencySymbol)
        val subTotalFormat = AmountUtil.format(subTotal,currencySymbol)
        val totalDueFormat = AmountUtil.format(totalDue,currencySymbol)
        val deliveryFeeFormat = AmountUtil.format(invoice.deliveryFee,currencySymbol)
        binding.apply {
            rv.adapter = invoiceDetailAdapter
            tvInvoiceNo.text = getString(R.string.invoice_no_km_en_format,invoiceId)
            tvCustomer.text = getString(R.string.customer_km_en_format,customer.fullName)
            tvDate.text = getString(R.string.date_km_en_format, DateUtil.format(invoice.date))
            tvCustomerPhoneNumber.text = getString(R.string.phone_number_km_en_format,customer.phoneNumber)
            tvCustomerAddress.text = getString(R.string.address_en_km_format,customer.address.ifEmpty { "- - -" })
            showDiscount(invoice.discount,subTotal)
            showAddition(subTotal,tvSubTotal,getString(R.string.sub_total_km_en_format,subTotalFormat))
            showAddition(totalDue,tvTotalDue,getString(R.string.total_due_km_en_format,totalDueFormat))
            showAddition(invoice.deliveryFee,tvDeliveryFee,getString(R.string.delivery_fee_km_en_format,deliveryFeeFormat))
        }
        updatePaymentSummary()
        invoiceDetailAdapter.submitList(items)
    }

    private fun initView(invoice: Invoice){
        binding.apply {
            mtb.setNavigationOnClickListener { finish() }
            btEdit.setOnClickListener { navigateToManageInvoiceActivity() }
            btPaymentHistory.setOnClickListener { showPaymentHistoryDialog() }
            btDelete.setOnClickListener { dialogUtil.showDelete { invoiceViewModel.delete(invoice) } }
            btSend.setOnClickListener {
                imageUtil.send(invoiceView,getString(R.string.invoice_no_format,invoiceId))
            }
        }
        binding.btPayment.setOnClickListener {
            showAddPaymentDialog(invoiceId,totalDue - totalPaid,currencySymbol)
        }
    }

    private fun showPaymentHistoryDialog(){
        val paymentHistoryDialogFragment = PaymentHistoryDialogFragment.newInstance(invoiceId,currencySymbol)
        paymentHistoryDialogFragment.show(supportFragmentManager,paymentHistoryDialogFragment.tag)
    }

    private fun navigateToManageInvoiceActivity(){
        navigationUtil.navigateTo(
            ManageInvoiceActivity::class.java,
            bundleOf(
                FORM_ADD_KEY to false,
                INVOICE_ID_KEY to invoiceId,
                TOTAL_PAID_KEY to totalPaid,
                DISPLAY_SUMMARY_KEY to displaySummary
            )
        )
        finish()
    }

    private fun showBusinessProfile(){
        val address = prefs.getString("address","") ?: ""
        val businessName = prefs.getString("business_name","") ?: ""
        showBusinessName_Address(binding.tvAddress,address)
        showBusinessName_Address(binding.tvBusinessName,businessName)
        showAllPhoneNumbers()
    }

    private fun showBusinessName_Address(tv: TextView, value:String){
        if(value.isEmpty()){
            tv.isVisible = false
        }
        else{
            tv.text = value
        }
    }

    private fun showAllPhoneNumbers(){
        var allPhoneNumbers = ""
        val primaryPhoneNumber = prefs.getString("primary_phone_number","") ?: ""
        val tertiaryPhoneNumber = prefs.getString("tertiary_phone_number","") ?: ""
        val secondaryPhoneNumber = prefs.getString("secondary_phone_number","") ?: ""
        if(primaryPhoneNumber.isNotEmpty()){
            allPhoneNumbers += primaryPhoneNumber
        }
        if(secondaryPhoneNumber.isNotEmpty()){
            allPhoneNumbers += " / $secondaryPhoneNumber"
        }
        if(tertiaryPhoneNumber.isNotEmpty()){
            allPhoneNumbers += " / $tertiaryPhoneNumber"
        }
        if(allPhoneNumbers.isEmpty()){
            binding.tvAllPhoneNumbers.isVisible = false
        }
        else{
            binding.tvAllPhoneNumbers.text = allPhoneNumbers
        }
    }

    private fun showDiscount(discount:Int,subTotal:Double){
        if(discount == 0){
            binding.tvDiscount.isVisible = false
        }
        else{
            val subTotalAfterDiscount = - (subTotal * discount / 100)
            val subTotalAfterDiscountFormat = AmountUtil.format(subTotalAfterDiscount,currencySymbol)
            binding.tvDiscount.text = getString(R.string.discount_km_en_format,discount,subTotalAfterDiscountFormat)
        }
    }

    private fun showAddition(value: Double, tv: TextView,valueFormat: String){
        if(value == 0.0){
            tv.isVisible = false
        }
        else{
            tv.text = valueFormat
        }
    }

    private fun updatePaymentSummary(){
        if(displaySummary){
            val balance = totalDue - totalPaid
            val balanceFormat = AmountUtil.format(balance,currencySymbol)
            val totalPaidFormat = AmountUtil.format(totalPaid,currencySymbol)
            binding.tvTotalDue.setTypeface(null,Typeface.NORMAL)
            binding.tvBalance.apply {
                isVisible = true
                text = getString(R.string.balance_km_en_format,balanceFormat)
            }
            binding.tvTotalPaid.apply {
                isVisible = true
                text = getString(R.string.total_paid_km_en_format,totalPaidFormat)
            }
            binding.tvPaidType.apply {
                isVisible = true
                text = getPaidType(balance)
            }
        }
    }

    private fun getPaidType(balance: Double): String{
        val paidType =  when{
            balance > 0 -> getString(R.string.unpaid)
            balance < 0 -> getString(R.string.over_payment)
            else -> getString(R.string.paid)
        }
        return getString(R.string.paid_type_format,paidType)
    }

    private fun showAddPaymentDialog(invoiceId:Long, balance: Double, currencySymbol: String){
        val addPaymentDialogFragment = AddPaymentDialogFragment.newInstance(invoiceId,balance,currencySymbol)
        addPaymentDialogFragment.show(supportFragmentManager,addPaymentDialogFragment.tag)
    }

    private fun showToast(message: String?){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun observeDeleteInvoice(){
        invoiceViewModel.message.observe(this) { result ->
            binding.pb.isVisible = true
            result.onSuccess { message ->
               showToast(message)
            }.onFailure { e ->
                binding.pb.isVisible = false
                dialogUtil.showMessage(e.message)
            }
        }
    }
}