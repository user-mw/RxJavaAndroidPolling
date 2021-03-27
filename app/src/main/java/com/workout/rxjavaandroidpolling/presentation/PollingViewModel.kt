package com.workout.rxjavaandroidpolling.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workout.rxjavaandroidpolling.domain.PollingUseCase

class PollingViewModel : ViewModel() {

    private val useCase = PollingUseCase()

    private val _pollingValue = MutableLiveData<String>()
    val pollingValue: LiveData<String> = _pollingValue

    private val _loadingInProgress = MutableLiveData<Boolean>()
    val loadingInProgress: LiveData<Boolean> = _loadingInProgress

    fun performPolling() {

    }
}