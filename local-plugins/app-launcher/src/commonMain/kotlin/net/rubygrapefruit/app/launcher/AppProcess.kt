@file:OptIn(ExperimentalAtomicApi::class)

package net.rubygrapefruit.app.launcher

import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private val propagateFailure = AtomicBoolean(false)

fun propagateMainFailure() {
    propagateFailure.store(true)
}

val isPropagateMainFailure: Boolean
    get() = propagateFailure.load()

fun finishMain(failure: Throwable?): Nothing {
    if (failure != null) {
        if (propagateFailure.load()) {
            throw failure
        }
        failure.printStackTrace()
        exit(1)
    } else {
        exit(0)
    }
}

expect fun exit(status: Int): Nothing
