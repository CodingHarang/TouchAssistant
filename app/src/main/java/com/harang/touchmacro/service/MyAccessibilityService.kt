package com.harang.touchmacro.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo


class MyAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("onAccessibilityEvent", "onAccessibilityEvent")
        event?.source?.apply {
            Log.e("event", event.toString())

            // Use the event and node information to determine
            // what action to take

            // take action on behalf of the user
//            performAction(AccessibilityNodeInfo.ACTION_CLICK)

            // recycle the nodeInfo object
            recycle()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("onServiceConnected", "onServiceConnected")
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
        info.notificationTimeout = 100
        info.packageNames = null
        serviceInfo = info
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}