package com.gifft.wrapping

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers

val clickUnconstrained: ViewAction
    get() = ViewActions.actionWithAssertions(
        object : ViewAction {
            override fun getDescription() = "click unconstrained"
            override fun getConstraints() = ViewMatchers.isEnabled()
            override fun perform(uiController: UiController?, view: View?) {
                view!!.performClick()
            }
        }
    )
