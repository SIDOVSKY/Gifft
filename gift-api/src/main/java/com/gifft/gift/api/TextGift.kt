package com.gifft.gift.api

import java.util.*

data class TextGift(
    override val uuid: String,
    override val sender: String,
    override val receiver: String,
    override val date: Date,
    override val type: GiftType,
    val text: String
) : Gift(uuid, sender, receiver, date, type)
