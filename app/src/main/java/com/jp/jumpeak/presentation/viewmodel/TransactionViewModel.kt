package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.data.repository.TransactionRepository
import com.jp.jumpeak.enums.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : BaseViewModel() {
    private val _query = MutableLiveData<Pair<String, Boolean>>()
    private val _transactionById = MutableLiveData<Result<Transaction>>()
    val transactionById: LiveData<Result<Transaction>> get() = _transactionById
    val transactionByPartiesId get() = _query.switchMap { (partiesId,isNew) ->
        transactionRepository.getByPartiesId(partiesId,isNew).cachedIn(viewModelScope)
    }
    val summaryByPartiesId get() = _query.switchMap { (partiesId,isNew) ->
        transactionRepository.getSummaryByPartiesId(partiesId,isNew)
    }

    fun updateQuery(partiesId: String,isSettled: Boolean){
        _query.value = Pair(partiesId,isSettled)
    }

    fun getById(id: Long){
        viewModelScope.launch {
            _transactionById.value = transactionRepository.getById(id)
        }
    }

    fun manage(action: Action,transaction: Transaction,isClear: Boolean){
        viewModelScope.launch {
            _manage.value = transactionRepository.manage(action,transaction,isClear)
        }
    }

    fun exportToExcel(partiesId: String,partiesName: String,isSettled: Boolean){
        viewModelScope.launch {
            _export.value = transactionRepository.exportToExcel(partiesId,partiesName,isSettled)
        }
    }
}