package com.gifft.gift.api

import io.reactivex.Observable

interface GiftRepository {
    fun allReceivedGifts() : Observable<List<TextGift>>
    fun allCreatedGifts(): Observable<List<TextGift>>

    fun saveTextGift(gift: TextGift)
    fun findGift(uuid: String) : TextGift?
    fun deleteGift(uuid: String)
}
