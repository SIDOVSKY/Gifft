package com.gifft.wrapping

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import com.gifft.core.api.toNavBundle
import com.gifft.gift.api.GiftType
import com.gifft.gift.api.TextGiftLinkBuilder
import com.gifft.wrapping.api.WrappingNavParam
import com.nhaarman.mockitokotlin2.argThat
import org.junit.Assert.*
import kotlinx.coroutines.CoroutineScope
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.isEmptyString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowAlertDialog
import java.util.*

@RunWith(RobolectricTestRunner::class)
class WrappingFragmentTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var giftRepository: GiftRepository

    @Test
    fun `should have empty fields when opened with missing nav parameter guid`() {
        val expectedGuid = null

        val scenario = launchFragmentInContainer(
            WrappingNavParam(expectedGuid).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam, coroutineScope: CoroutineScope) =
                    WrappingViewModel(
                        navParam,
                        coroutineScope,
                        giftRepository,
                        Mockito.mock(TextGiftLinkBuilder::class.java)
                    )
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
            with(fragment.viewBinding!!) {
                assertThat(sender.text.toString(), isEmptyString())
                assertThat(receiver.text.toString(), isEmptyString())
                assertThat(giftText.text.toString(), isEmptyString())
            }
        }
    }

    @Test
    fun `should preset fields when opened with filled nav parameter guid`() {
        val expectedUuid = "Some UUID"
        val expectedSender = "Sender"
        val expectedReceiver = "Receiver"
        val expectedGiftText = "Text"

        Mockito.`when`(giftRepository.findGift(expectedUuid))
            .thenReturn(
                TextGift(
                    expectedUuid,
                    expectedSender,
                    expectedReceiver,
                    Date(),
                    GiftType.Created,
                    expectedGiftText
                )
            )

        val scenario = launchFragmentInContainer(
            WrappingNavParam(expectedUuid).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam, coroutineScope: CoroutineScope) =
                    WrappingViewModel(
                        navParam,
                        coroutineScope,
                        giftRepository,
                        Mockito.mock(TextGiftLinkBuilder::class.java)
                    )
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
            with(fragment.viewBinding!!) {
                assertEquals(expectedSender, sender.text.toString())
                assertEquals(expectedReceiver, receiver.text.toString())
                assertEquals(expectedGiftText, giftText.text.toString())
            }
        }
    }

    @Test
    fun `should show alert dialog on back press after editing`() {
        val scenario = launchFragmentInContainer(
            WrappingNavParam(null).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam, coroutineScope: CoroutineScope) =
                    WrappingViewModel(
                        navParam,
                        coroutineScope,
                        giftRepository,
                        Mockito.mock(TextGiftLinkBuilder::class.java)
                    )
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
            with(fragment.viewBinding!!) {
                giftText.setText("NEW TEXT")
            }
            fragment.requireActivity().onBackPressed()
        }

        assertEquals(1, ShadowAlertDialog.getShownDialogs().size)
    }

    @Test
    fun `should not show alert dialog on back press without editing`() {
        val scenario = launchFragmentInContainer(
            WrappingNavParam(null).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam, coroutineScope: CoroutineScope) =
                    WrappingViewModel(
                        navParam,
                        coroutineScope,
                        giftRepository,
                        Mockito.mock(TextGiftLinkBuilder::class.java)
                    )
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment {
            it.requireActivity().onBackPressed()
        }

        assertThat(ShadowAlertDialog.getShownDialogs(), `is`(empty()))
    }

    @Test
    fun `should save new filled gift`() {
        val expectedSender = "Sender"
        val expectedReceiver = "Receiver"
        val expectedGiftText = "Text"

        val scenario = launchFragmentInContainer(
            WrappingNavParam(null).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam, coroutineScope: CoroutineScope) =
                    WrappingViewModel(
                        navParam,
                        coroutineScope,
                        giftRepository,
                        Mockito.mock(TextGiftLinkBuilder::class.java)
                    )
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
            with(fragment.viewBinding!!) {
                sender.setText(expectedSender)
                receiver.setText(expectedReceiver)
                giftText.setText(expectedGiftText)
            }

            fragment.requireActivity().onBackPressed()
        }

        // Unconstrained due to a false java.lang.RuntimeException:
        // ... view does not match one or more of the following constraints:
        // at least 90 percent of the view's area is displayed to the user.
        onView(withText(R.string.save)).inRoot(isDialog()).perform(clickUnconstrained)

        verify(giftRepository).saveTextGift(argThat {
            sender == expectedSender
                    && receiver == expectedReceiver
                    && text == expectedGiftText
        })
    }

    @Test
    fun `should delete cleaned gift`() {
        val expectedUuid = "Some UUID"

        Mockito.`when`(giftRepository.findGift(expectedUuid))
            .thenReturn(
                TextGift(
                    expectedUuid,
                    "Initial sender",
                    "Initial receiver",
                    Date(),
                    GiftType.Created,
                    "Initial gift text"
                )
            )

        val scenario = launchFragmentInContainer(
            WrappingNavParam(expectedUuid).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam, coroutineScope: CoroutineScope) =
                    WrappingViewModel(
                        navParam,
                        coroutineScope,
                        giftRepository,
                        Mockito.mock(TextGiftLinkBuilder::class.java)
                    )
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment { fragment ->
            with(fragment.viewBinding!!) {
                sender.setText("")
                receiver.setText("")
                giftText.setText("")
            }

            fragment.requireActivity().onBackPressed()
        }

        // Unconstrained due to a false java.lang.RuntimeException:
        // ... view does not match one or more of the following constraints:
        // at least 90 percent of the view's area is displayed to the user.
        onView(withText(R.string.delete)).inRoot(isDialog()).perform(clickUnconstrained)

        verify(giftRepository).deleteGift(expectedUuid)
    }
}
