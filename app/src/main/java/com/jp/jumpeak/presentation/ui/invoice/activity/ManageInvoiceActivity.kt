package com.jp.jumpeak.presentation.ui.invoice.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Invoice
import com.jp.jumpeak.data.entity.Item
import com.jp.jumpeak.data.pojo.InvoicePojo
import com.jp.jumpeak.databinding.ActivityManageInvoiceBinding
import com.jp.jumpeak.enums.Currency
import com.jp.jumpeak.presentation.ui.customer.fragment.CustomerListDialogFragment
import com.jp.jumpeak.presentation.ui.invoice.adapter.ItemAdapter
import com.jp.jumpeak.presentation.viewmodel.InvoiceViewModel
import com.jp.jumpeak.util.objects.DatePickerDialogUtil
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.classes.NavigationUtil
import com.jp.jumpeak.util.classes.NumberFormatUtil
import com.jp.jumpeak.util.objects.AmountUtil
import com.jp.jumpeak.util.objects.DateUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import com.jp.jumpeak.util.objects.WindowInsertsListenerUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class ManageInvoiceActivity : AppCompatActivity() {
    private var customerId: Long ?= null
    private var items = mutableListOf<Item>()
    private val dialogUtil by lazy { DialogUtil(this) }
    private val navigationUtil by lazy { NavigationUtil(this) }
    private val invoiceViewModel: InvoiceViewModel by viewModels()
    private val invoiceId by lazy { intent.getLongExtra(INVOICE_ID_KEY,0) }
    private val formAdd by lazy { intent.getBooleanExtra(FORM_ADD_KEY,true) }
    private val binding by lazy { ActivityManageInvoiceBinding.inflate(layoutInflater) }
    private val datePicker by lazy {
        DatePickerDialogUtil.setup{ date ->
            binding.etDate.setText(DateUtil.format(date))
        }
    }
    private val itemAdapter by lazy {
        ItemAdapter(this){ item,isEditMode -> setupAction(item,isEditMode) }
    }

    private fun setupAction(item: Item,isEditMode:Boolean){
        if(isEditMode){
            binding.apply {
                etUnit.setText(item.unit)
                etGoods.setText(item.goods)
                etQty.setText(item.qty.toString())
                etPrice.setText(AmountUtil.format(item.price).trim())
            }
        }
        items.remove(item)
        itemAdapter.submitList(items.toList())
        if(items.isEmpty()) updateButtonDone(false)
    }

    companion object{
        private const val FORM_ADD_KEY = "form_add"
        private const val FULL_NAME_KEY = "full_name"
        private const val INVOICE_ID_KEY = "invoice_id"
        private const val TOTAL_PAID_KEY = "total_paid"
        private const val CUSTOMER_ID_KEY = "customer_id"
        private const val CUSTOMER_RESULT = "customer_result"
        private const val DISPLAY_SUMMARY_KEY = "display_summary"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        getFragmentResult()
        observeInvoiceById()
        observeManageInvoice()
        WindowInsertsListenerUtil.setup(binding.main)
    }

    private fun initView(){
        setupToolbar()
        setupValidation()
        val calendar = Calendar.getInstance()
        binding.apply {
            rv.adapter = itemAdapter
            etDate.setText(DateUtil.format(calendar.time.time))
            etPrice.addTextChangedListener(NumberFormatUtil(etPrice))
            etDeliveryFee.addTextChangedListener(NumberFormatUtil(etDeliveryFee))
            rbKHR.setOnClickListener { itemAdapter.setCurrency(Currency.KHR.symbol) }
            rbUSD.setOnClickListener { itemAdapter.setCurrency(Currency.USD.symbol) }
            etDate.setOnClickListener{ datePicker.show(supportFragmentManager,"Date Picker") }
            btAdd.setOnClickListener { addItem() }
            btDone.setOnClickListener { submitInvoice() }
            etCustomer.setOnClickListener {
                val customerListDialogFragment = CustomerListDialogFragment()
                customerListDialogFragment.show(supportFragmentManager,customerListDialogFragment.tag)
            }
        }
    }

    private fun setupToolbar() {
        val title = if (formAdd) getString(R.string.add_invoice) else getString(R.string.edit)
        binding.mtb.apply {
            this.title = title
            setNavigationOnClickListener { finish() }
        }
    }

    private fun setupValidation() {
        val textWatcher = TextWatcherUtil.setup {
            val qty = binding.etQty.text.toString()
            val date = binding.etDate.text.toString()
            val price = binding.etPrice.text.toString()
            val unit = binding.etUnit.text.toString().trim()
            val customer = binding.etCustomer.text.toString()
            val goods = binding.etGoods.text.toString().trim()
            val isEnabled = customer.isNotEmpty() && goods.isNotEmpty() && qty.isNotEmpty() &&
                            unit.isNotEmpty() && price.isNotEmpty() && date.isNotEmpty()
            binding.btAdd.apply {
                this.isEnabled = isEnabled
                alpha = if (isEnabled) 1f else 0.5f
            }
        }
        binding.apply {
            etQty.addTextChangedListener(textWatcher)
            etUnit.addTextChangedListener(textWatcher)
            etDate.addTextChangedListener(textWatcher)
            etGoods.addTextChangedListener(textWatcher)
            etPrice.addTextChangedListener(textWatcher)
            etCustomer.addTextChangedListener(textWatcher)
        }
    }

    private fun submitInvoice(){
        val date = binding.etDate.text.toString()
        val currency = if(binding.rbKHR.isChecked) Currency.KHR else Currency.USD
        val discount = binding.etDiscount.text.toString().toIntOrNull() ?: 0
        val deliveryFee = binding.etDeliveryFee.text.toString().replace(",","").trim().toDoubleOrNull() ?: 0.0
        val subTotal = items.sumOf { item -> item.amount }
        val totalDue = subTotal - (subTotal * discount / 100) + deliveryFee
        val invoice = Invoice(
            invoiceId,
            customerId!!,
            discount,
            deliveryFee,
            totalDue,
            DateUtil.toDate(date)?.time!!,
            currency,
        )
        invoiceViewModel.manage(invoice,items)
    }

    private fun getFragmentResult(){
        supportFragmentManager.setFragmentResultListener(CUSTOMER_RESULT,this){ _,bundle ->
            customerId = bundle.getLong(CUSTOMER_ID_KEY)
            val fullName = bundle.getString(FULL_NAME_KEY)
            binding.etCustomer.setText(fullName)
        }
    }

    private fun addItem(){
        val qty = binding.etQty.text.toString().toInt()
        val unit = binding.etUnit.text.toString().trim()
        val goods = binding.etGoods.text.toString().trim()
        val price = binding.etPrice.text.toString().replace(",","").trim().toDouble()
        if(items.any { item -> item.goods.equals(goods,true) }){
            dialogUtil.showMessage(getString(R.string.goods_already_exist_message,goods))
            return
        }
        items.add(Item(invoiceId = 0, goods = goods,qty = qty, unit = unit, price = price,amount = qty * price))
        if(items.size == 1) updateButtonDone(true)
        itemAdapter.submitList(items.toList())
        clearForm()
    }

    private fun updateButtonDone(isEnabled:Boolean){
        binding.rv.isVisible = isEnabled
        binding.btDone.apply {
            this.isEnabled = isEnabled
            alpha = if(isEnabled) 1f else 0.5f
        }
    }

    private fun clearForm(){
        binding.apply {
            etQty.setText("")
            etUnit.setText("")
            etPrice.setText("")
            etGoods.setText("")
        }
    }

    private fun observeInvoiceById(){
        if(formAdd) return
        invoiceViewModel.getById(invoiceId)
        invoiceViewModel.obj.observe(this) { result ->
            showLoading(true)
            result.onSuccess { invoicePojo ->
                showLoading(false)
                fillForm(invoicePojo)
            }.onFailure { e ->
                showToast(e.message)
            }
        }
    }

    private fun showLoading(isVisible:Boolean){
        binding.apply {
            pb.isVisible = isVisible
            btAdd.isEnabled = !isVisible
            btDone.isEnabled = !isVisible
            div.alpha = if(isVisible) 0.5f else 1f
        }
    }

    private fun fillForm(invoicePojo: InvoicePojo){
        val invoice = invoicePojo.invoice
        customerId = invoice.customerId
        val currencySymbol = invoice.currency.symbol
        updateButtonDone(true)
        binding.apply {
            etDate.setText(DateUtil.format(invoice.date))
            etDiscount.setText(invoice.discount.toString())
            etCustomer.setText(invoicePojo.customer.fullName)
            rbKHR.isChecked = currencySymbol == Currency.KHR.symbol
            rbUSD.isChecked = currencySymbol == Currency.USD.symbol
            etDeliveryFee.setText(AmountUtil.format(invoice.deliveryFee).trim())
        }
        items = invoicePojo.items.toMutableList()
        itemAdapter.apply {
            submitList(items.toList())
            setCurrency(currencySymbol)
        }
    }

    private fun showToast(message: String?){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun observeManageInvoice(){
        val totalPaid = intent.getDoubleExtra(TOTAL_PAID_KEY,0.0)
        val displaySummary = intent.getBooleanExtra(DISPLAY_SUMMARY_KEY,true)
        invoiceViewModel.manageInvoice.observe(this) { result ->
            showLoading(true)
            result.onSuccess { invoiceId ->
                navigationUtil.navigateTo(
                    InvoiceDetailActivity::class.java,
                    bundleOf(
                        INVOICE_ID_KEY to invoiceId,
                        TOTAL_PAID_KEY to totalPaid,
                        DISPLAY_SUMMARY_KEY to displaySummary
                    )
                )
                finish()
            }.onFailure { e ->
                showLoading(false)
                dialogUtil.showMessage(e.message)
            }
        }
    }
}