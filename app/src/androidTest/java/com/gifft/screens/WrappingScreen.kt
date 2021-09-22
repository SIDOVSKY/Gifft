package com.gifft.screens

import io.github.kakaocup.kakao.dialog.KAlertDialog
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.screen.Screen
import com.gifft.R

class WrappingScreen : Screen<WrappingScreen>() {
    val from = KEditText { withId(R.id.sender) }
    val to = KEditText { withId(R.id.receiver) }
    val giftText = KEditText { withId(R.id.giftText) }
    val backPressAlert = KAlertDialog()
}
