package com.gifft.wrapping.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WrappingNavParam(val existingGiftUuid: String?) : Parcelable
