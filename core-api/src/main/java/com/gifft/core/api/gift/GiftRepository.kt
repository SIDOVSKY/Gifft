package com.gifft.core.api.gift

interface GiftRepository {
    fun allReceivedGifts() : List<Gift>
}
