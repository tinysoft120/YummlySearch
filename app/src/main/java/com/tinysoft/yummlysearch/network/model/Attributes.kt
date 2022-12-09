package com.tinysoft.yummlysearch.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attributes(
    val course: List<String>,
    val cuisine: List<String>
): Parcelable