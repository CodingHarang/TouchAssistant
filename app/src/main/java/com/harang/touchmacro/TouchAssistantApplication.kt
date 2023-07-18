package com.harang.touchmacro

import android.app.Application
import android.content.Context

class TouchAssistantApplication : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: TouchAssistantApplication
        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }
}