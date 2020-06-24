package com.gifft.gift

import com.gifft.gift.api.TextGift

internal fun TextGiftEntity.toTextGift() = TextGift(uuid, sender, receiver, date, text)
