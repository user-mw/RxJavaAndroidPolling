package com.workout.rxjavaandroidpolling.domain

import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleSource
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Predicate
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicReference

class SecondPollingUseCase {

	operator fun invoke(
		attemptsCount: Int,
		predicate: Predicate<String> = Predicate { value -> value == "40" || value == "00" },
		signalValue: String = "Finished", // Just for manual testing
	): Single<String> {
		return getTime()
			.repeatWithCondition(attemptsCount,	predicate, signalValue)
			.subscribeOn(Schedulers.io())
	}

	private fun getTime(): Single<String> {
		return Single.fromCallable(CustomCallable()) // or SingleFromCallable(CustomCallable())
	}
}

private class CustomCallable : Callable<String> { // Just to be more obvious

	private companion object {
		const val PATTERN = "ss"
	}

	override fun call(): String {
		val formatter = SimpleDateFormat(PATTERN, Locale.ENGLISH)

		return formatter.format(System.currentTimeMillis())
	}
}

private fun <T : Any> Single<T>.repeatWithCondition(attemptsCount: Int, predicate: Predicate<T>, signalValue: T): Single<T> =
	RepeatSingle(this, attemptsCount, predicate, signalValue)

private class RepeatSingle<T : Any>(
	private val source: SingleSource<T>,
	private val attemptsCount: Int,
	private val predicate: Predicate<T>,
	private val signalValue: T,
) : Single<T>(), Action {

	private var done = false

	override fun subscribeActual(observer: SingleObserver<in T>) {
		for (index in 0 until attemptsCount) {
			if (done) {
				break
			}

			source.subscribe(RepeatSingleObserver(observer, predicate, this, signalValue))
		}
	}

	override fun run() {
		done = true
	}

	private class RepeatSingleObserver<T : Any>(
		private val actual: SingleObserver<in T>,
		private val predicate: Predicate<T>,
		private val finisher: Action,
		private val signalValue: T,
	) : AtomicReference<Disposable>(), Disposable, SingleObserver<T> {

		private lateinit var disposable: Disposable

		override fun onSubscribe(d: Disposable) {
			disposable = d
		}

		override fun onSuccess(t: T) {
			if (predicate.test(t)) {
				disposable.dispose()
				actual.onSuccess(signalValue)
				finisher.run()
				return
			}

			actual.onSuccess(t)

			Thread.sleep(1000)
		}

		override fun onError(e: Throwable) {
			actual.onError(e)
		}

		override fun dispose() {
			DisposableHelper.dispose(this)
		}

		override fun isDisposed(): Boolean =
			get() == DisposableHelper.DISPOSED
	}
}