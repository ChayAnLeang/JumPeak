package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jp.jumpeak.data.entity.Invoice
import com.jp.jumpeak.data.entity.Item
import com.jp.jumpeak.data.pojo.InvoicePojo
import com.jp.jumpeak.data.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository
): BaseViewModel<InvoicePojo>() {
    private val _manageInvoice = MutableLiveData<Result<Long>>()
    val manageInvoice:LiveData<Result<Long>> = _manageInvoice
    private val _query = MutableLiveData<Triple<Long,Long,String>>()
    val numberOfUnpaidInvoices = invoiceRepository.countUnpaidInvoices()
    val dailyInvoices = _query.switchMap { (startDate,endDate,customerName) ->
        invoiceRepository.getDailyInvoice(startDate,endDate,customerName).cachedIn(viewModelScope)
    }
    val allUnpaidInvoices = _query.switchMap { (_,_,customerName) ->
        invoiceRepository.getAllUnpaidInvoices(customerName).cachedIn(viewModelScope)
    }

    fun updateQuery(startDate: Long ?= null,endDate:Long ?= null,customerName: String ?= null){
        val currentEndDate = endDate ?: _query.value?.second ?: 0
        val currentStartDate = startDate ?: _query.value?.first ?: 0
        val currentCustomerName = customerName ?: _query.value?.third ?: ""
        _query.value = Triple(currentStartDate,currentEndDate,currentCustomerName)
    }

    fun getById(id: Long){
        viewModelScope.launch {
            _obj.value = invoiceRepository.getById(id)
        }
    }

    fun delete(invoice: Invoice){
        viewModelScope.launch {
            _message.value = invoiceRepository.delete(invoice)
        }
    }

    fun manage(invoice: Invoice,items: List<Item>){
        viewModelScope.launch {
            _manageInvoice.value = invoiceRepository.manage(invoice,items)
        }
    }
}