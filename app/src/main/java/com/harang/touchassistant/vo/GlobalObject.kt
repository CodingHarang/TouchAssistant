package com.harang.touchassistant.vo

import kotlinx.coroutines.flow.MutableStateFlow

object GlobalObject {
    val isOverlayShowing = MutableStateFlow(false)
    var testValue = true
    var isFullScreenShowing = false
    val isFullScreenShowing_flow = MutableStateFlow(false)
    var isRunning = false
    val isRunning_flow = MutableStateFlow(false)
    var isPaused = false
    var loopCount = 0
    val loopCount_flow = MutableStateFlow(0)
    val gestureArrayList: ArrayList<() -> Unit> = arrayListOf()
}