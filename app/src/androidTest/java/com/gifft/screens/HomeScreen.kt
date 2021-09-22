package com.gifft.screens

import android.view.View
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
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
