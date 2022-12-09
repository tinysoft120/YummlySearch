package com.tinysoft.yummlysearch.network.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Matche(
    val attributes: Attributes,
    val flavors: Flavors?,
    val id: String,
    val imageUrlsBySize: HashMap<String, String>,
    val ingredients: List<String>,
    val rating: Int,
    val recipeName: String,
    val smallImageUrls: List<String>?,
    val sourceDisplayName: String,
    val totalTimeInSeconds: Int
): Parcelable {
    @IgnoredOnParcel
    val totalTimeInMins: Int get() = totalTimeInSeconds / 60
}