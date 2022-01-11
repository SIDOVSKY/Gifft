package com.gifft.gift.api

import kotlinx.coroutines.flow.Flow

interface GiftRepository {
    fun allReceivedGifts() : Flow<List<TextGift>>
    fun allCreatedGifts(): Flow<List<TextGift>>

    fun saveTextGift(gift: TextGift)
    fun findGift(uuid: String) : TextGift?
    fun deleteGift(uuid: String)
}
