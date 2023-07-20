package com.harang.touchassistant.vo

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalObject {
    var testValue = true
    var isFullScreenShowing = false
    var isRunning = false
    val isRunning_flow = MutableStateFlow(false)
    var isPaused = false
    var loopCount = 1
    val loopCount_flow = MutableStateFlow(1)
}