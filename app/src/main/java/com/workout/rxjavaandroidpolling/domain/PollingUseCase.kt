package com.workout.rxjavaandroidpolling.domain

import io.reactivex.Single
import io.reactivex.internal.operators.single.SingleJust

class PollingUseCase {

    operator fun invoke(): Single<String> =
        SingleJust(System.currentTimeMillis().toString())
}