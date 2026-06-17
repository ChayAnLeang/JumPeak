package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.jp.jumpeak.data.entity.Payment
import com.jp.jumpeak.data.repository.PaymentRepository
import com.jp.jumpeak.enums.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : BaseViewModel<Payment>() {
    private val _invoiceId = MutableLiveData<Long>()
    val paymentsByInvoiceId = _invoiceId.switchMap { invoiceId ->
        paymentRepository.getByInvoiceId(invoiceId)
    }

    fun getById(id:Long){
        viewModelScope.launch {
            _obj.value = paymentRepository.getById(id)
        }
    }

    fun setInvoiceId(invoiceId:Long){
        _invoiceId.value = invoiceId
    }

    fun manage(payment: Payment,action: Action){
        viewModelScope.launch {
            _message.value = paymentRepository.manage(payment,action)
        }
    }
}