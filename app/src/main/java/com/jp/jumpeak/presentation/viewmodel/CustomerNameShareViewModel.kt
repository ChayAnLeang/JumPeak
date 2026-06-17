package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CustomerNameShareViewModel @Inject constructor(): ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    fun setName(name: String){
        _name.value = name
    }
}