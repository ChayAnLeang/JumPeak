package com.jp.jumpeak.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    protected val _manage = MutableLiveData<Result<String>>()
    val manage: LiveData<Result<String>> get() = _manage
    protected val _export = MutableLiveData<Result<String>>()
    val export: LiveData<Result<String>> get() = _export
}