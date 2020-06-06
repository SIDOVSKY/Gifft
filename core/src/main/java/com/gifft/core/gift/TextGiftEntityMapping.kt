package com.gifft.core.gift

import com.gifft.core.api.gift.TextGift

internal fun TextGiftEntity.toTextGift() = TextGift(uuid, sender, receiver, date, text)
