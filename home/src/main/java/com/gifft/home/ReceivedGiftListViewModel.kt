package com.gifft.home

import com.gifft.core.events.asFlow
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import com.gifft.unwrapping.api.UnwrappingNavParam
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ReceivedGiftListViewModel @AssistedInject constructor(
    @Assisted viewModelScope: CoroutineScope,
    giftRepository: GiftRepository,
) : GiftListViewModel(viewModelScope, giftRepository) {

    @AssistedFactory
    interface Factory : GiftListViewModel.Factory {
        override fun create(viewModelScope: CoroutineScope): ReceivedGiftListViewModel
    }

    private val _receivedGifts = MutableStateFlow<List<TextGift>>(emptyList())
    override val gifts = _receivedGifts.asStateFlow()

    init {
        launch {
            stateMutable.value = VisualState.IN_PROGRESS
            giftRepository.allReceivedGifts().collectIndexed { index, textGift ->
                _receivedGifts.value = textGift

                if (index == 0) {
                    stateMutable.value = VisualState.DEFAULT
                }
            }
        }
    }

    val openGiftCommand = openGiftEvent.asFlow().map { UnwrappingNavParam(it.uuid) }
}
