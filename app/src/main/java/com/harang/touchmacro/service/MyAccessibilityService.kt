package com.harang.touchmacro.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.harang.touchmacro.ui.overlay.OverlayScreen


class MyAccessibilityService : AccessibilityService() {

    private lateinit var composeView: ComposeView
    private lateinit var lifecycleOwner: MyLifecycleOwner

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.source?.apply {
            Log.e("event", "${event.packageName}, ${event.text}")
            if (event.packageName == "com.harang.touchmacro") {
                when (event.text.toString().replace("[", "").replace("]", "")) {
                    "Up" -> {
                        Log.e("up", "up")
                        val swipePath = Path()
                        swipePath.moveTo(640f, 500f)
                        swipePath.lineTo(640f, 1500f)
                        val gestureBuilder = GestureDescription.Builder()
                        gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 5000))
                        dispatchGesture(gestureBuilder.build(), null, null)

                    }
                    "Down" -> {
                        Log.e("down", "down")
                        val swipePath = Path()
                        swipePath.moveTo(540f, 1500f)
                        swipePath.lineTo(540f, 500f)
                        val gestureBuilder = GestureDescription.Builder()
                        gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 5000))
                        dispatchGesture(gestureBuilder.build(), null, null)
                    }
                    "Left" -> {
                        Log.e("left", "left")
                        val swipePath = Path()
                        swipePath.moveTo(10f, 900f)
                        swipePath.lineTo(1070f, 900f)
                        val gestureBuilder = GestureDescription.Builder()
                        gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 5000))
                        dispatchGesture(gestureBuilder.build(), null, null)
                    }
                    "Right" -> {
                        Log.e("right", "right")
                        val swipePath = Path()
                        swipePath.moveTo(1070f, 1000f)
                        swipePath.lineTo(10f, 1000f)
                        val gestureBuilder = GestureDescription.Builder()
                        gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 5000))
                        dispatchGesture(gestureBuilder.build(), null, null)
                    }
                }
            }
            // Use the event and node information to determine
            // what action to take

            // take action on behalf of the user
//            performAction(AccessibilityNodeInfo.ACTION_CLICK)

            // recycle the nodeInfo object
            recycle()
        }
    }

    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager

    private val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26
    } else {
        WindowManager.LayoutParams.TYPE_PHONE
    }

    private val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        layoutFlag,
        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE : 터치 이벤트를 받지 않는다.
        // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN : 화면에 가득 차게 한다.
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE : 포커스를 받지 않는다.
        // PixelFormat.TRANSLUCENT : 투명하게 한다.
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    fun changePosition(x: Int, y: Int) {
        params.x += x
        params.y += y
        windowManager.updateViewLayout(composeView, params)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("onServiceConnected", "onServiceConnected")
//        val info = AccessibilityServiceInfo()
//        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
//        info.notificationTimeout = 100
//        info.packageNames = null
//        serviceInfo = info
        composeView = ComposeView(this)
        lifecycleOwner = MyLifecycleOwner()
        showOverlay(
            stopSelf = {
                lifecycleOwner.setCurrentState(Lifecycle.State.DESTROYED)
                stopSelf()
            }
        )

    }



    private fun showOverlay(
        stopSelf: () -> Unit
    ) {
        composeView.setContent {
            OverlayScreen(
                changePosition = { x: Int, y: Int ->
                    changePosition(x, y)
                },
                swipe = {
                    Log.e("swipe", "swipe")
                    val swipePath = Path()
                    swipePath.moveTo(1000f, 1000f)
                    swipePath.lineTo(100f, 1000f)
                    val gestureBuilder = GestureDescription.Builder()
                    gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 100))
                    dispatchGesture(gestureBuilder.build(), null, null)
                },
                stopSelf = stopSelf
            )
        }

        // Trick The ComposeView into thinking we are tracking lifecycle
        val viewModelStore = ViewModelStore()
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
//        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        windowManager.addView(composeView, params)
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }

//    private fun configureSwipeButton() {
//        val swipeButton = mLayout.findViewById(R.id.swipe) as Button
//        swipeButton.setOnClickListener {
//            val swipePath = Path()
//            swipePath.moveTo(1000, 1000)
//            swipePath.lineTo(100, 1000)
//            val gestureBuilder = GestureDescription.Builder()
//            gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 500))
//            dispatchGesture(gestureBuilder.build(), null, null)
//        }
//    }
}