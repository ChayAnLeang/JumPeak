package com.jp.jumpeak.presentation.ui.customer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.jp.jumpeak.R
import com.jp.jumpeak.data.entity.Customer
import com.jp.jumpeak.databinding.ActivityManageCustomerBinding
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.presentation.viewmodel.CustomerViewModel
import com.jp.jumpeak.util.classes.DialogUtil
import com.jp.jumpeak.util.objects.TextWatcherUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageCustomerActivity : AppCompatActivity() {
    private val dialogUtil by lazy { DialogUtil(this) }
    private val customerViewModel: CustomerViewModel by viewModels()
    private val customerId by lazy { intent.getLongExtra(CUSTOMER_ID_KEY,0) }
    private val formAdd by lazy { intent.getBooleanExtra(FORM_ADD_KEY,true) }
    private val binding by lazy { ActivityManageCustomerBinding.inflate(layoutInflater) }

    companion object{
        private const val FORM_ADD_KEY = "form_add"
        private const val CUSTOMER_ID_KEY = "customer_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        observeCustomerById()
        observeManageCustomer()
    }

    private fun initView(){
        setupToolbar()
        setupValidation()
        binding.btSave.setOnClickListener { submitCustomer() }
    }

    private fun setupToolbar(){
        val title = if(formAdd) getString(R.string.add_customer) else getString(R.string.edit)
        binding.mtb.apply {
            this.title = title
            setNavigationOnClickListener { finish() }
        }
    }

    private fun setupValidation(){
        val textWatcher = TextWatcherUtil.setup {
            val phoneNumber = binding.etPhoneNumber.text.toString()
            val fullName = binding.etFullName.text.toString().trim()
            val isEnabled = phoneNumber.isNotEmpty() && fullName.isNotEmpty()
            binding.btSave.apply {
                this.isEnabled = isEnabled
                alpha = if(isEnabled) 1f else 0.5f
            }
        }
        binding.apply {
            etFullName.addTextChangedListener(textWatcher)
            etPhoneNumber.addTextChangedListener(textWatcher)
        }
    }

    private fun submitCustomer(){
        val address = binding.etAddress.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString()
        val fullName = binding.etFullName.text.toString().trim()
        val customer = Customer(customerId,fullName,phoneNumber,address)
        val action = if(formAdd) Action.ADD else Action.EDIT
        customerViewModel.manage(customer,action)
    }

    private fun observeCustomerById(){
        if(formAdd) return
        customerViewModel.getById(customerId)
        customerViewModel.obj.observe(this) { result ->
            showLoading(true)
            result.onSuccess { customer ->
                showLoading(false)
                fillForm(customer)
            }.onFailure { e ->
                showToast(e.message)
            }
        }
    }

    private fun showLoading(isVisible:Boolean){
        binding.apply {
            pb.isVisible = isVisible
            btSave.isEnabled = !isVisible
            div.alpha = if(isVisible) 0.5f else 1f
        }
    }

    private fun fillForm(customer: Customer){
        binding.apply {
            etAddress.setText(customer.address)
            etFullName.setText(customer.fullName)
            etPhoneNumber.setText(customer.phoneNumber)
        }
        binding.btDelete.apply {
            isVisible = true
            setOnClickListener {
                dialogUtil.showDelete { customerViewModel.manage(customer, Action.DELETE) }
            }
        }
    }

    private fun showToast(message: String?){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun observeManageCustomer(){
        customerViewModel.message.observe(this) { result ->
            showLoading(true)
            result.onSuccess { message ->
                showToast(message)
            }.onFailure { e ->
                showLoading(false)
                showDialog(e.message)
            }
        }
    }

    private fun showDialog(message:String?){
        if(message?.contains("UNIQUE") == true){
            val phoneNumber = binding.etPhoneNumber.text.toString()
            dialogUtil.showMessage(getString(R.string.phone_already_exist_message,phoneNumber))
            return
        }
        dialogUtil.showMessage(message)
    }
}