package com.gifft.core.gift

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
