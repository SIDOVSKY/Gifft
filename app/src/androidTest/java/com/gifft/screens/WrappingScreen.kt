package com.gifft.screens

import com.agoda.kakao.dialog.KAlertDialog
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.gifft.R

class WrappingScreen : Screen<WrappingScreen>() {
    val from = KEditText { withId(R.id.sender) }
    val to = KEditText { withId(R.id.receiver) }
    val giftText = KEditText { withId(R.id.giftText) }
    val backPressAlert = KAlertDialog()
}
