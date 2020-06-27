package com.gifft.gift.api

import android.net.Uri

interface TextGiftLinkBuilder {
    suspend fun build(gift: TextGift) : String?
    suspend fun parse(encodedGift: Uri) : TextGift?
}
