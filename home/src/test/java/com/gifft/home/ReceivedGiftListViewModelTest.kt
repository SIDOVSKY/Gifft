package com.gifft.home

import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ReceivedGiftListViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var giftRepository: GiftRepository

    @Test
    fun `should load gift list after creation`() = runTest {
        val expectedGifts = listOf(
            TextGift("", "", "", Date(), GiftType.Unknown, "")
        )

        Mockito.`when`(giftRepository.allReceivedGifts()).thenReturn(flow {
            delay(1_000)
            emit(expectedGifts)
        })

        val loadedGiftsSequence = mutableListOf<Any>()
        val stateSequence = mutableListOf<Any>()
        val viewModel = ReceivedGiftListViewModel(viewModelScope = this, giftRepository)
        launch {
            viewModel.gifts.toList(loadedGiftsSequence)
        }
        launch {
            viewModel.state.toList(stateSequence)
        }
        advanceUntilIdle()
        coroutineContext.job.cancelChildren()

        assertEquals(
            listOf(
                GiftListViewModel.VisualState.IN_PROGRESS,
                GiftListViewModel.VisualState.DEFAULT
            ),
            stateSequence
        )
        assertEquals(
            listOf(
                emptyList(),
                expectedGifts
            ),
            loadedGiftsSequence
        )
    }
}
