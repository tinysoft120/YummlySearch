package com.tinysoft.yummlysearch.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.navOptions
import com.tinysoft.yummlysearch.MainActivity
import com.tinysoft.yummlysearch.R

/**
 * Base Fragment in main activity
 * */
abstract class AbsMainFragment(@LayoutRes layout: Int) : Fragment(layout) {

    val mainActivity: MainActivity
        get() = activity as MainActivity

    // navigation option with animation
    val navOptions by lazy {
        navOptions {
            launchSingleTop = false
            anim {
                enter = R.anim.fragment_open_enter
                exit = R.anim.fragment_open_exit
                popEnter = R.anim.fragment_close_enter
                popExit = R.anim.fragment_close_exit
            }
        }
    }
}