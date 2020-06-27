package com.gifft.gift

import com.gifft.gift.api.GiftType
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.*

@Entity
internal data class TextGiftEntity(
    @Id var id: Long = 0,
    val uuid: String,
    val text: String,
    val date: Date,
    val sender: String,
    val receiver: String,

    @Convert(converter = GiftTypeEnumOrdinalConverter::class, dbType = Integer::class)
    val type: GiftType
)
