package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jp.jumpeak.data.entity.Customer
import com.jp.jumpeak.data.repository.CustomerRepository
import com.jp.jumpeak.enums.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : BaseViewModel<Customer>() {
    private val _name = MutableLiveData<String>("")
    val customersByName = _name.switchMap { name ->
        customerRepository.getByName(name).cachedIn(viewModelScope)
    }

    fun setName(name: String){
        _name.value = name
    }

    fun getById(id: Long){
        viewModelScope.launch {
            _obj.value = customerRepository.getById(id)
        }
    }

    fun manage(customer: Customer,action: Action){
        viewModelScope.launch {
            _message.value = customerRepository.manage(customer,action)
        }
    }
}