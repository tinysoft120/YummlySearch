package com.tinysoft.yummlysearch.ui.search

import android.os.Parcelable
import com.tinysoft.yummlysearch.network.model.Matche
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchViewState(
    val query: String?,
    val totalCount: Int,
    val newAddedCount: Int,
    val items: List<Matche>
): Parcelable {

    @IgnoredOnParcel
    val offset get() = items.size

    companion object {
        val EMPTY = SearchViewState(null, 0, 0, listOf())
    }
}