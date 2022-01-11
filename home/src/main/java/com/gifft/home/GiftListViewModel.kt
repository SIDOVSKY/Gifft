package com.gifft.home

import com.gifft.core.events.BufferingEvent
import com.gifft.core.debounce
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class GiftListViewModel constructor(
    private val viewModelScope: CoroutineScope,
    private val giftRepository: GiftRepository,
) : CoroutineScope by viewModelScope {

    interface Factory {
        fun create(viewModelScope: CoroutineScope): GiftListViewModel
    }

    enum class VisualState {
        DEFAULT,
        IN_PROGRESS,
    }

    protected val openGiftEvent = BufferingEvent<TextGift>()

    protected val stateMutable = MutableStateFlow(VisualState.DEFAULT)

    val state = stateMutable.asStateFlow()

    abstract val gifts: StateFlow<List<TextGift>>

    fun onOpenGiftClick(giftToOpen: TextGift) {
        if (debounce) return
        openGiftEvent.send(giftToOpen)
    }

    fun onDeleteButtonClick(giftToDelete: TextGift) {
        if (debounce) return
        giftRepository.deleteGift(giftToDelete.uuid)
    }
}
