package com.tinysoft.yummlysearch.ui.components

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessScrollListener : RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold = 2

    // True if we are still waiting for the last set of data to load.
    private var loading = true

    var mLayoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: LinearLayoutManager) {
        mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for ((i, pos) in lastVisibleItemPositions.withIndex()) {
            if (i == 0) {
                maxSize = pos
            } else if (pos > maxSize) {
                maxSize = pos
            }
        }
        return maxSize
    }

    /**
     * This happens many times a second during a scroll, so be wary of the code you place here.
     * We are given a few useful parameters to help us work out if we need to load some more data,
     * but first we check if we are waiting for the previous load to finish.
     * */
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {

        var lastVisibleItemPosition = 0
        val totalItemCount = mLayoutManager.itemCount

        when (val lm = mLayoutManager) {
            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = lm.findLastVisibleItemPositions(null)
                // get maximum element within the list
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
            }
            is GridLayoutManager -> {
                lastVisibleItemPosition = lm.findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                lastVisibleItemPosition = lm.findLastVisibleItemPosition()
            }
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            onLoadMore(totalItemCount, view)
            loading = true
        }
    }

    fun finishLoading() {
        loading = false
    }

    /**
     * Defines the process for actually loading more data based on page
     * */
    abstract fun onLoadMore(totalItemsCount: Int, view: RecyclerView)
}