package com.gifft.core.api

import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
class Debounce(
    var interval: Duration = Duration.milliseconds(500),
    private var timeSource: TimeSource = TimeSource.Monotonic,
) {
    companion object {
        val global = Debounce()
    }

    private var finishTime = timeSource.markNow()

    var disabled = false

    fun checkAndStart(): Boolean {
        if (disabled)
            return false

        if (finishTime.hasNotPassedNow())
            return true

        finishTime = timeSource.markNow() + interval
        return false
    }
}

val debounce
    get() = Debounce.global.checkAndStart()