package com.gifft.wrapping.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WrappingNavParam(val existingGiftUuid: String?) : Parcelable
