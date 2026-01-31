package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jp.jumpeak.data.entity.Parties
import com.jp.jumpeak.data.entity.Transaction
import com.jp.jumpeak.data.repository.PartiesRepository
import com.jp.jumpeak.enums.Action
import com.jp.jumpeak.enums.PartiesType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PartiesViewModel @Inject constructor(
    private val partiesRepository: PartiesRepository
) : BaseViewModel() {
    private val _partiesType = MutableLiveData<PartiesType>(PartiesType.OWE_ME)
    private val _partiesById = MutableLiveData<Result<Parties>>()
    val partiesById: LiveData<Result<Parties>> get() = _partiesById
    val partiesByPartiesType get() = _partiesType.switchMap { partiesType ->
        partiesRepository.getByPartiesType(partiesType).cachedIn(viewModelScope)
    }

    fun setPartiesType(partiesType: PartiesType){
        _partiesType.value = partiesType
    }

    fun getById(id: String){
        viewModelScope.launch {
            _partiesById.value = partiesRepository.getById(id)
        }
    }

    fun manage(action: Action,parties: Parties,transaction: Transaction ?= null){
        viewModelScope.launch {
            _manage.value = partiesRepository.manage(action,parties,transaction)
        }
    }

    fun exportToExcel(partiesType: PartiesType){
        viewModelScope.launch {
            _export.value = partiesRepository.exportToExcel(partiesType)
        }
    }
}