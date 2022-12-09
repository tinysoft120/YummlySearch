package com.tinysoft.yummlysearch.ui.components

/**
 * Loading State to display progressing API calling
 * */
sealed class LoadingState {
    object Loading: LoadingState()
    object Idle: LoadingState()
    data class LoadFailure(val errorMsg: String): LoadingState()
}