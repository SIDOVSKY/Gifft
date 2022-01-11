package com.gifft.home

import com.gifft.core.debounce
import com.gifft.core.events.asReceiveOnly
import com.gifft.core.events.BufferingEvent
import javax.inject.Inject

class HomeViewModel @Inject constructor() {
    private val _openWrappingCommand = BufferingEvent()
    val openWrappingCommand = _openWrappingCommand.asReceiveOnly()

    fun onWrapButtonClick() {
        if (debounce) return
        _openWrappingCommand.send()
    }
}
