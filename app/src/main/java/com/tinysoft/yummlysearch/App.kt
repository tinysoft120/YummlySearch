package com.tinysoft.yummlysearch

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // start Koin
        startKoin {
            androidContext(this@App)
            modules(appModules)
        }
    }

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }
    }

}