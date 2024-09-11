package com.example.voicechanger.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>()
    var isLoading : LiveData<Boolean> = _isLoading
}