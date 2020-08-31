package com.gifft.gift

import com.gifft.gift.api.GiftType
import io.objectbox.converter.PropertyConverter

class GiftTypeEnumOrdinalConverter :
    PropertyConverter<GiftType, Int> {

    override fun convertToDatabaseValue(entityProperty: GiftType): Int {
        return entityProperty.ordinal
    }

    override fun convertToEntityProperty(databaseValue: Int): GiftType {
        return GiftType.values()[databaseValue]
    }
}
