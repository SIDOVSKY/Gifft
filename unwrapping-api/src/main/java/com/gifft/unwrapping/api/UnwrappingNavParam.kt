package com.gifft.unwrapping.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.net.URI

@Parcelize
data class UnwrappingNavParam
/**
 * @constructor for parceling
 * Please use secondary constructors
 */
    (
    val uri: URI?,
    val text: String?
) : Parcelable {
    constructor(uri: URI) : this(uri, null)

    constructor(text: String) : this(null, text)
}
