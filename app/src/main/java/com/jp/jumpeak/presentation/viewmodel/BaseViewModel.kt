package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {
    protected val _obj = MutableLiveData<Result<T>>()
    val obj: LiveData<Result<T>> = _obj
    protected val _message = MutableLiveData<Result<String>>()
    val message: LiveData<Result<String>> = _message
}