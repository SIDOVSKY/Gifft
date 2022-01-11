package com.gifft.home

import com.gifft.core.events.asFlow
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import com.gifft.wrapping.api.WrappingNavParam
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class CreatedGiftListViewModel @AssistedInject constructor(
    @Assisted viewModelScope: CoroutineScope,
    private val giftRepository: GiftRepository,
) : GiftListViewModel(viewModelScope, giftRepository) {

    @AssistedFactory
    interface Factory : GiftListViewModel.Factory {
        override fun create(viewModelScope: CoroutineScope): CreatedGiftListViewModel
    }

    private val _createdGifts = MutableStateFlow<List<TextGift>>(emptyList())
    override val gifts = _createdGifts.asStateFlow()

    init {
        launch {
            stateMutable.value = VisualState.IN_PROGRESS
            giftRepository.allCreatedGifts().collectIndexed { index, textGift ->
                _createdGifts.value = textGift

                if (index == 0) {
                    stateMutable.value = VisualState.DEFAULT
                }
            }
        }
    }

    val openGiftCommand = openGiftEvent.asFlow().map { WrappingNavParam(it.uuid) }
}
