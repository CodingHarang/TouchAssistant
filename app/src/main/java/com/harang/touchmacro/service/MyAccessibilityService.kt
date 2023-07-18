package com.harang.touchmacro.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.harang.touchmacro.ui.overlay.OverlayScreen
import com.harang.touchmacro.vo.GlobalObject


class MyAccessibilityService : AccessibilityService() {

    private lateinit var composeView: ComposeView
    private lateinit var lifecycleOwner: MyLifecycleOwner
    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager
    private val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 26
    } else {
        WindowManager.LayoutParams.TYPE_PHONE
    }

    private val params1 = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        layoutFlag,
        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE : 터치 이벤트를 받지 않는다.
        // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN : 화면에 가득 차게 한다.
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE : 포커스를 받지 않는다.
        // PixelFormat.TRANSLUCENT : 투명하게 한다.
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    private val params2 = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        layoutFlag,
        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE : 터치 이벤트를 받지 않는다.
        // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN : 화면에 가득 차게 한다.
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE : 포커스를 받지 않는다.
        // PixelFormat.TRANSLUCENT : 투명하게 한다.
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("event", "${event}\n${event?.text}\n${event?.text.toString().replace("[", "").replace("]", "")}")
        if (event != null) {
            Log.e("eventNotNull", "eventNotNull\npackageName = ${event.packageName}")
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
    }

    private fun changePosition(x: Int, y: Int) {
        if (!GlobalObject.isFullScreenShowing) {
            params1.x += x
            params1.y += y
            windowManager.updateViewLayout(composeView, params1)
        }
    }

    private fun updateIsFullScreen(isFull: Boolean) {
        if (isFull) {
//            params2.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            windowManager.updateViewLayout(composeView, params2)
        } else {
            windowManager.updateViewLayout(composeView, params1)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("onServiceConnected", "onServiceConnected")
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
                updateIsFullScreen = {
                    updateIsFullScreen(it)
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
        windowManager.addView(composeView, params1)
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