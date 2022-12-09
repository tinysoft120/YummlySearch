package com.tinysoft.yummlysearch.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Flavors(
    val bitter: Double,
    val meaty: Double,
    val piquant: Double,
    val salty: Double,
    val sour: Double,
    val sweet: Double
): Parcelable