package com.harang.touchmacro.data

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalConstants {
    val SHARED_PREFERENCES_NAME = "touch_assistant_shared_preferences"
    var isLooping = true
    var loopCount = MutableStateFlow(1)
}