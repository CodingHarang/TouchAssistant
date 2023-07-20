package com.harang.touchmacro.data

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalConstants {
    val SHARED_PREFERENCES_NAME = "touch_assistant_shared_preferences"
    var isLooping = true
    var isPaused = false
    val loopCount_1 = MutableStateFlow(1)
    var loopCount_2 = 1
}