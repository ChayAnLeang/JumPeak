package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jp.jumpeak.data.entity.Reminder
import com.jp.jumpeak.data.repository.ReminderRepository
import com.jp.jumpeak.enums.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
) : BaseViewModel() {
    private val _reminderById = MutableLiveData<Result<Reminder>>()
    val reminderById: LiveData<Result<Reminder>> get() = _reminderById
    val allReminders get() = reminderRepository.getAll().cachedIn(viewModelScope)

    fun getById(id: Long){
        viewModelScope.launch {
            _reminderById.value = reminderRepository.getById(id)
        }
    }

    fun manage(action: Action,reminder: Reminder){
        viewModelScope.launch {
            _manage.value = reminderRepository.manage(action,reminder)
        }
    }
}