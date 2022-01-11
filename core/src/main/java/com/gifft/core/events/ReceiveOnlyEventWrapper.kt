package com.gifft.core.events

private class ReceiveOnlyEventWrapper<T>(private val originalEventSource: EventSource<T>) :
    EventSource<T> by originalEventSource

fun <T> EventSource<T>.asReceiveOnly(): EventSource<T> = ReceiveOnlyEventWrapper(this)