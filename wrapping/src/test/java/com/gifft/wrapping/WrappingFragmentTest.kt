package com.gifft.wrapping

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.gifft.gift.api.GiftRepository
import com.gifft.gift.api.TextGift
import com.gifft.core.api.toNavBundle
import com.gifft.wrapping.api.WrappingNavParam
import com.nhaarman.mockitokotlin2.argThat
import org.junit.Assert.*
import kotlinx.android.synthetic.main.wrapping_fragment.*
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
                override fun create(navParam: WrappingNavParam) =
                    WrappingViewModel(navParam, giftRepository)
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment {
            assertThat(it.sender.text.toString(), isEmptyString())
            assertThat(it.receiver.text.toString(), isEmptyString())
            assertThat(it.giftText.text.toString(), isEmptyString())
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
                    expectedGiftText
                )
            )

        val scenario = launchFragmentInContainer(
            WrappingNavParam(expectedUuid).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam) =
                    WrappingViewModel(navParam, giftRepository)
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment {
            assertEquals(expectedSender, it.sender.text.toString())
            assertEquals(expectedReceiver, it.receiver.text.toString())
            assertEquals(expectedGiftText, it.giftText.text.toString())
        }
    }

    @Test
    fun `should show alert dialog on back press after editing`() {
        val scenario = launchFragmentInContainer(
            WrappingNavParam(null).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam) =
                    WrappingViewModel(navParam, giftRepository)
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment {
            it.giftText.setText("NEW TEXT")
            it.requireActivity().onBackPressed()
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
                override fun create(navParam: WrappingNavParam) =
                    WrappingViewModel(navParam, giftRepository)
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
                override fun create(navParam: WrappingNavParam) =
                    WrappingViewModel(navParam, giftRepository)
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment {
            it.sender.setText(expectedSender)
            it.receiver.setText(expectedReceiver)
            it.giftText.setText(expectedGiftText)

            it.requireActivity().onBackPressed()
        }

        // Unconstrained due to a false java.lang.RuntimeException:
        // ... view does not match one or more of the following constraints:
        // at least 90 percent of the view's area is displayed to the user.
        onView(withText(R.string.save)).inRoot(isDialog()).perform(clickUnconstrained)

        verify(giftRepository).saveCreatedTextGift(argThat {
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
                    "Initial gift text"
                )
            )

        val scenario = launchFragmentInContainer(
            WrappingNavParam(expectedUuid).toNavBundle(),
            R.style.AppTheme
        ) {
            WrappingFragment(object : WrappingViewModel.Factory {
                override fun create(navParam: WrappingNavParam) =
                    WrappingViewModel(navParam, giftRepository)
            })
        }

        scenario.moveToState(Lifecycle.State.RESUMED)
        scenario.onFragment {
            it.sender.setText("")
            it.receiver.setText("")
            it.giftText.setText("")

            it.requireActivity().onBackPressed()
        }

        // Unconstrained due to a false java.lang.RuntimeException:
        // ... view does not match one or more of the following constraints:
        // at least 90 percent of the view's area is displayed to the user.
        onView(withText(R.string.delete)).inRoot(isDialog()).perform(clickUnconstrained)

        verify(giftRepository).deleteGift(expectedUuid)
    }
}
