package com.gifft.home

import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import io.reactivex.subjects.PublishSubject
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.*

class CreatedGiftListViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var giftRepository: GiftRepository

    @Test
    fun `should load gift list after creation`() {
        val expectedGifts = listOf(
            TextGift("", "", "", Date(), "")
        )

        val allCreatedGiftsSubject = PublishSubject.create<List<TextGift>>()

        Mockito.`when`(giftRepository.allCreatedGifts())
            .thenReturn(allCreatedGiftsSubject)

        val createdGiftViewModel = CreatedGiftListViewModel(giftRepository)

        val giftsSubscriber = createdGiftViewModel.gifts.test()
        val stateSubscriber = createdGiftViewModel.state.test()

        allCreatedGiftsSubject.onNext(expectedGifts)

        giftsSubscriber.assertValuesOnly(expectedGifts).dispose()
        stateSubscriber
            .assertValuesOnly(
                GiftListViewModel.VisualState.IN_PROGRESS,
                GiftListViewModel.VisualState.DEFAULT
            )
            .dispose()
    }
}
