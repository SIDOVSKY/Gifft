package com.gifft.screens

import android.view.View
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.gifft.R
import org.hamcrest.Matcher

class HomeScreen : Screen<HomeScreen>() {
    val wrapButton = KButton { withId(R.id.wrapButton) }

    var createdList = KRecyclerView({
        withId(R.id.giftList)
    }, itemTypeBuilder = {
        itemType(::CreatedGiftItem)
    })

    class CreatedGiftItem(parent: Matcher<View>) : KRecyclerItem<CreatedGiftItem>(parent) {
        val receiver = KTextView(parent) { withId(R.id.receiver) }
        val date = KTextView(parent) { withId(R.id.date) }
        val giftText = KTextView(parent) { withId(R.id.giftContent) }
        val delete = KTextView(parent) { withId(R.id.deleteLayout) }
    }
}
