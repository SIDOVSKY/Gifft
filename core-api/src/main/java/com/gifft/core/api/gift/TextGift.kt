package com.gifft.core.api.gift

import java.util.*

data class TextGift(
    override val uuid: String,
    override val sender: String,
    override val receiver: String,
    override val date: Date,
    val text: String
) : Gift(uuid, sender, receiver, date)
