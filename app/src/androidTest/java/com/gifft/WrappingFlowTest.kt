package com.gifft

import androidx.test.espresso.action.ViewActions
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gifft.home.HomeActivity
import com.gifft.screens.HomeScreen
import com.gifft.screens.WrappingScreen
import io.github.kakaocup.kakao.screen.Screen.Companion.onScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WrappingFlowTest {

    @get:Rule
    val rule = activityScenarioRule<HomeActivity>()

    @Test
    fun should_wrap_save_delete() {
        val sender = "Sender"
        val receiver = "Receiver"
        val giftText = "Gift Text"

        onScreen<HomeScreen> {
            createdList { hasSize(0) }
            wrapButton { click() }
        }

        onScreen<WrappingScreen> {
            from { typeText(sender) }
            to { typeText(receiver) }
            closeSoftKeyboard()
            giftText { typeText(giftText) }
            pressBack() // For soft keyboard
            pressBack() // For exit
            backPressAlert {
                positiveButton { click() }
            }
        }

        onScreen<HomeScreen> {
            createdList {
                hasSize(1)
                firstChild<HomeScreen.CreatedGiftItem> {
                    isDisplayed()
                    receiver { hasText(receiver) }
                    giftText { hasText(giftText) }
                    act { ViewActions.swipeLeft() }
                    delete { click() }
                }
                hasSize(0)
            }
        }
    }
}
