package com.workout.rxjavaandroidpolling.domain

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.single.SingleJust
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DEFAULT_VALUE = "There is no desired value"
private const val HARDCODED_VALUE = "00"

/**
 * Rough useCase, allowing to perform polling. Will take first n values or finish when get desired hardcoded value with 1 second pause.
 */
class RoughPollingUseCase {

    operator fun invoke(times: Int): Single<String> =
        Observable.range(1, times)
            .flatMap {
                SingleJust(getNiceTime()).toObservable()
            }
            .takeUntil { seconds -> seconds == HARDCODED_VALUE }
            .toSpecialObservable()
            .last(DEFAULT_VALUE)
            .subscribeOn(Schedulers.io())

    private fun getNiceTime(): String {
        val dateFormat = SimpleDateFormat("ss", Locale.ENGLISH)

        return dateFormat.format(Date(System.currentTimeMillis()))
    }
}

private fun Observable<String>.toSpecialObservable() : Observable<String> =
    SpecialObservable(this)

private class SpecialObservable(private val source: ObservableSource<String>) : Observable<String>() {

    override fun subscribeActual(observer: Observer<in String>) {
        source.subscribe(SpecialObserver(observer))
    }
}

private class SpecialObserver(private val actual: Observer<in String>) : Observer<String> {

    private var first = false
	private var attempt = 0 // For testing

    override fun onSubscribe(d: Disposable) {
        actual.onSubscribe(d)
    }

    override fun onNext(t: String) {
		attempt++

        if (!first) {
            first = true
        } else {
            Thread.sleep(1000)
        }

		if (t == HARDCODED_VALUE) {
			actual.onNext("Got $t with attempt #$attempt")
		} else {
			actual.onNext(DEFAULT_VALUE)
		}
    }

    override fun onError(e: Throwable) {
        actual.onError(e)
    }

    override fun onComplete() {
        actual.onComplete()
    }
}