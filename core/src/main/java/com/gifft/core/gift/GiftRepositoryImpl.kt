package com.gifft.core.gift

import com.gifft.core.api.gift.Gift
import com.gifft.core.api.gift.GiftRepository

class GiftRepositoryImpl : GiftRepository {
    override fun allReceivedGifts(): List<Gift> {
        return emptyList()
    }
}
