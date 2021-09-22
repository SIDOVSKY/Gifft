package com.gifft.unwrapping.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnwrappingNavParam (val uriOrUuid: String) : Parcelable
