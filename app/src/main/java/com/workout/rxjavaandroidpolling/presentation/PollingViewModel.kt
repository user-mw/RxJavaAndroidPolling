package com.workout.rxjavaandroidpolling.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workout.rxjavaandroidpolling.domain.RoughPollingUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class PollingViewModel : ViewModel() {

	private companion object {
		const val DEFAULT_VALUE = "There is no desired value"
	}

    private val compositeDisposable = CompositeDisposable()

    private val useCase = RoughPollingUseCase()

    private val _pollingValue = MutableLiveData<String>()
    val pollingValue: LiveData<String> = _pollingValue

    private val _loadingInProgress = MutableLiveData<Boolean>()
    val loadingInProgress: LiveData<Boolean> = _loadingInProgress

    fun performPolling() {
        useCase(10)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { value -> handleValue(value) },
                { error -> println(error.message) },
            )
            .addTo(compositeDisposable)
    }

    private fun handleValue(value: String) {
    	if (value == DEFAULT_VALUE) {
    		_loadingInProgress.value = true
			_pollingValue.value = ""
		} else {
			_loadingInProgress.value = false
			_pollingValue.value = value
		}
    }

    fun clearCompositeDisposable() {
        compositeDisposable.dispose()
    }
}