package com.gifft.core.events

interface EventChannel<T> : EventSource<T> {
    fun send(value: T)
}

interface UnitEventChannel : EventChannel<Unit> {
    fun send() = send(Unit)
}
