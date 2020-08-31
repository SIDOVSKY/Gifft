package com.gifft.wrapping

import org.junit.Assert.*
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGift
import com.gifft.gift.api.TextGiftLinkBuilder
import com.gifft.wrapping.api.WrappingNavParam
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.*

class WrappingViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var giftRepository: GiftRepository

    @ExperimentalCoroutinesApi
    @Test
    fun `should load existing gift from parameter`() {
        val expectedSender = "Sender"
        val expectedReceiver = "Receiver"
        val expectedGiftContent = "GiftContent"

        `when`(giftRepository.findGift(Mockito.anyString()))
            .thenReturn(
                TextGift(
                    "",
                    expectedSender,
                    expectedReceiver,
                    Date(),
                    GiftType.Created,
                    expectedGiftContent
                )
            )

        val wrappingViewModel = WrappingViewModel(
            WrappingNavParam("123"),
            TestCoroutineScope(),
            giftRepository,
            Mockito.mock(TextGiftLinkBuilder::class.java)
        )

        wrappingViewModel.sender
            .test()
            .assertValue(expectedSender)
            .dispose()

        wrappingViewModel.receiver
            .test()
            .assertValue(expectedReceiver)
            .dispose()

        wrappingViewModel.giftContent
            .test()
            .assertValue(expectedGiftContent)
            .dispose()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should be empty without existing gift parameter`() {
        val expectedSender = ""
        val expectedReceiver = ""
        val expectedGiftContent = ""

        val wrappingViewModel = WrappingViewModel(
            WrappingNavParam(null),
            TestCoroutineScope(),
            giftRepository,
            Mockito.mock(TextGiftLinkBuilder::class.java)
        )

        wrappingViewModel.sender
            .test()
            .assertValue(expectedSender)
            .dispose()

        wrappingViewModel.receiver
            .test()
            .assertValue(expectedReceiver)
            .dispose()

        wrappingViewModel.giftContent
            .test()
            .assertValue(expectedGiftContent)
            .dispose()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should have NoChanges exit mode right after creation`() {
        val wrappingViewModel = WrappingViewModel(
            WrappingNavParam(null),
            TestCoroutineScope(),
            giftRepository,
            Mockito.mock(TextGiftLinkBuilder::class.java)
        )

        assertEquals(WrappingViewModel.ExitMode.NoChanges, wrappingViewModel.exitMode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should have New exit mode after sender input without gift parameter`() {
        val wrappingViewModel = WrappingViewModel(
            WrappingNavParam(null),
            TestCoroutineScope(),
            giftRepository,
            Mockito.mock(TextGiftLinkBuilder::class.java)
        )

        wrappingViewModel.senderInput.accept("NEW SENDER")

        assertEquals(WrappingViewModel.ExitMode.New, wrappingViewModel.exitMode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should have New exit mode after receiver input without gift parameter`() {
        val wrappingViewModel = WrappingViewModel(
            WrappingNavParam(null),
            TestCoroutineScope(),
            giftRepository,
            Mockito.mock(TextGiftLinkBuilder::class.java)
        )

        wrappingViewModel.receiverInput.accept("NEW RECEIVER")

        assertEquals(WrappingViewModel.ExitMode.New, wrappingViewModel.exitMode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should have New exit mode after gift content input without gift parameter`() {
        val wrappingViewModel = WrappingViewModel(
            WrappingNavParam(null),
            TestCoroutineScope(),
            giftRepository,
            Mockito.mock(TextGiftLinkBuilder::class.java)
        )

        wrappingViewModel.giftContentInput.accept("NEW CONTENT")

        assertEquals(WrappingViewModel.ExitMode.New, wrappingViewModel.exitMode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should have Edited exit mode after gift content input with existing gift parameter`() {
        `when`(giftRepository.findGift(Mockito.anyString()))
            .thenReturn(
                TextGift("", "OLD SENDER", "OLD RECEIVER", Date(), GiftType.Created, "OLD CONTENT")
            )

        val wrappingViewModel = WrappingViewModel(
            WrappingNavParam("123"),
            TestCoroutineScope(),
            giftRepository,
            Mockito.mock(TextGiftLinkBuilder::class.java)
        )

        wrappingViewModel.giftContentInput.accept("NEW CONTENT")

        assertEquals(WrappingViewModel.ExitMode.Edited, wrappingViewModel.exitMode)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should have Cleaned exit mode after empty inputs with existing gift parameter`() {
        `when`(giftRepository.findGift(Mockito.anyString()))
            .thenReturn(
                TextGift("", "OLD SENDER", "OLD RECEIVER", Date(), GiftType.Created, "OLD CONTENT")
            )

        val wrappingViewModel = WrappingViewModel(
            WrappingNavParam("123"),
            TestCoroutineScope(),
            giftRepository,
            Mockito.mock(TextGiftLinkBuilder::class.java)
        )

        wrappingViewModel.senderInput.accept("")
        wrappingViewModel.receiverInput.accept("")
        wrappingViewModel.giftContentInput.accept("")

        assertEquals(WrappingViewModel.ExitMode.Cleaned, wrappingViewModel.exitMode)
    }
}
