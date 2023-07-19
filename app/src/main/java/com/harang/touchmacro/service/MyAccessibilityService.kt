package com.harang.touchmacro.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
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
import com.harang.touchmacro.data.GlobalConstants
import com.harang.touchmacro.provider.SharedPreferencesManager
import com.harang.touchmacro.ui.overlay.OverlayScreen
import com.harang.touchmacro.vo.GlobalObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.TimeUnit


class MyAccessibilityService : AccessibilityService() {

    private lateinit var composeView: ComposeView
    private lateinit var lifecycleOwner: MyLifecycleOwner
    private val windowManager get() = getSystemService(WINDOW_SERVICE) as WindowManager
    private val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
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
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
        PixelFormat.TRANSLUCENT
    )

    private val params2 = WindowManager.LayoutParams(
        SharedPreferencesManager.getInt("screen_width") / 2,
        SharedPreferencesManager.getInt("screen_height"),
        layoutFlag,
        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE : 터치 이벤트를 받지 않는다.
        // WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN : 화면에 가득 차게 한다.
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE : 포커스를 받지 않는다.
        // PixelFormat.TRANSLUCENT : 투명하게 한다.
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    @OptIn(DelicateCoroutinesApi::class)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.e("event", "${event}\n${event?.text}\n${event?.text.toString().replace("[", "").replace("]", "")}")
        if (event != null) {
            Log.e("eventNotNull", "eventNotNull\npackageName = ${event.packageName}")
            when (event.text.toString().replace("[", "").replace("]", "")) {
                "Up" -> {
                    Log.e("up", "up")
                    GlobalConstants.isLooping = true
                    val swipePath = Path()
                    val executor = newSingleThreadContext("executor")
                    executor.executor.execute() {
                        while (GlobalConstants.isLooping) {
                            // 1 big cycle
                            for (i in 1 .. 40) {
                                Log.e("start", "start")
                                // 1st skill
                                if (!GlobalConstants.isLooping) break
                                dispatchTapGesture(415f, 1400f, 200, 200, 1)

                                // tap cycle 2 times
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)

                                // 2nd skill
                                dispatchTapGesture(540f, 1400f, 200, 200, 1)

                                // tap cycle 2 times
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)

                                // 3rd skill
                                dispatchTapGesture(175f, 1400f, 200, 200, 1)

                                // tap cycle 2 times
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)

                                // 4th skill
                                if (!GlobalConstants.isLooping) break
                                dispatchTapGesture(55f, 1400f, 200, 200, 1)

                                // tap cycle 2 times
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)

                                // 5th skill
                                if (!GlobalConstants.isLooping) break
                                dispatchTapGesture(300f, 1400f, 200, 200, 1)

                                // tap cycle 2 times
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)
                                dispatchTapGesture(355f, 500f, 20, 5, 50)
                                dispatchTapGesture(355f, 1160f, 20, 5, 50)
                                dispatchTapGesture(355f, 740f, 20, 5, 50)

                                // warrior window
                                dispatchTapGesture(175f, 1525f, 200, 400, 1)

                                // slide down
                                dispatchDragGesture(355f, 1200f, 355f, 1400f, 400, 400, 1)

                                // 1st warrior upgrade
                                dispatchTapGesture(600f, 1200f, 50, 50, 5)

                                // 2nd warrior upgrade
                                dispatchTapGesture(600f, 1300f, 50, 50, 5)

                                // 3rd warrior upgrade
                                dispatchTapGesture(600f, 1400f, 50, 50, 5)

                                // warrior window
                                dispatchTapGesture(175f, 1525f, 200, 200, 1)
                                Log.e("end", "end")
                            }

                            // prestige window
                            dispatchTapGesture(50f, 1525f, 200, 200, 1)

                            // prestige
                            dispatchTapGesture(600f, 1250f, 200, 200, 1)

                            // prestige ok
                            dispatchTapGesture(355f, 1300f, 200, 15000, 1)

                            // prestige window
                            dispatchTapGesture(50f, 1525f, 200, 200, 1)

                            // wide window
                            dispatchTapGesture(575f, 885f, 200, 200, 1)

                            // 1st skill upgrade - wide window
                            dispatchTapGesture(600f, 700f, 200, 200, 1)
                            dispatchTapGesture(460f, 700f, 200, 200, 1)

                            // 2nd skill upgrade - wide window
                            dispatchTapGesture(600f, 820f, 200, 200, 1)
                            dispatchTapGesture(460f, 820f, 200, 200, 1)

                            // 3rd skill upgrade - wide window
                            dispatchTapGesture(600f, 940f, 200, 200, 1)
                            dispatchTapGesture(460f, 940f, 200, 200, 1)

                            // 4th skill upgrade - wide window
                            dispatchTapGesture(600f, 1060f, 200, 200, 1)
                            dispatchTapGesture(460f, 1060f, 200, 200, 1)

                            // 5th skill upgrade - wide window
                            dispatchTapGesture(600f, 1180f, 200, 200, 1)
                            dispatchTapGesture(460f, 1180f, 200, 200, 1)

                            // 6th skill upgrade - wide window
                            dispatchTapGesture(600f, 1300f, 200, 200, 1)
                            dispatchTapGesture(460f, 1300f, 200, 200, 1)

                            // wide window
                            dispatchTapGesture(575f, 885f, 200, 200, 1)

                            // artifact window
                            dispatchTapGesture(540f, 1510f, 200, 200, 1)

                            // all artifact upgrade
                            dispatchTapGesture(590f, 1150f, 50, 50, 5)

                            // artifact window
                            dispatchTapGesture(50f, 1525f, 200, 200, 1)
                        }
//                            swipePath.moveTo(640f, 500f)
//                            swipePath.lineTo(640f, 500f)
//                            val gestureBuilder = GestureDescription.Builder()
//                            gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 20))
//                            dispatchGesture(gestureBuilder.build(), null, null)
//                            TimeUnit.MILLISECONDS.sleep(25)
                    }
                }

                "Down" -> {
                    Log.e("down", "down")
                    GlobalConstants.isLooping = false
//                    val swipePath = Path()
//                    swipePath.moveTo(540f, 1500f)
//                    swipePath.lineTo(540f, 500f)
//                    val gestureBuilder = GestureDescription.Builder()
//                    gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 1000))
//                    dispatchGesture(gestureBuilder.build(), null, null)
                }

                "Left" -> {
                    Log.e("left", "left")
                    val swipePath = Path()
                    swipePath.moveTo(10f, 900f)
                    swipePath.lineTo(1070f, 900f)
                    val gestureBuilder = GestureDescription.Builder()
                    gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 1000))
                    dispatchGesture(gestureBuilder.build(), null, null)
                }

                "Right" -> {
                    Log.e("right", "right")
                    val swipePath = Path()
                    swipePath.moveTo(1070f, 1000f)
                    swipePath.lineTo(10f, 1000f)
                    val gestureBuilder = GestureDescription.Builder()
                    gestureBuilder.addStroke(StrokeDescription(swipePath, 0, 1000))
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
            params2.x = 0
            params2.y = SharedPreferencesManager.getInt("status_bar_height")
            params2.gravity = Gravity.START
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
    private fun dispatchTapGesture(moveToX: Float, moveToY: Float, duration: Long, delay: Long, repeat: Long) {
        for (i in 1..repeat) {
            if (!GlobalConstants.isLooping) break
            val swipePath = Path()
            swipePath.moveTo(moveToX, moveToY)
            swipePath.lineTo(moveToX, moveToY)
            val gestureBuilder = GestureDescription.Builder()
            gestureBuilder.addStroke(StrokeDescription(swipePath, 0, duration))
            dispatchGesture(gestureBuilder.build(), null, null)
            TimeUnit.MILLISECONDS.sleep(duration + delay)
        }
    }

    private fun dispatchDragGesture(moveToX: Float, moveToY: Float, lineToX: Float, lineToY: Float, duration: Long, delay: Long, repeat: Long) {
        for (i in 1..repeat) {
            if (!GlobalConstants.isLooping) break
            val swipePath = Path()
            swipePath.moveTo(moveToX, moveToY)
            swipePath.lineTo(lineToX, lineToY)
            val gestureBuilder = GestureDescription.Builder()
            gestureBuilder.addStroke(StrokeDescription(swipePath, 0, duration))
            dispatchGesture(gestureBuilder.build(), null, null)
            TimeUnit.MILLISECONDS.sleep(duration + delay)
        }
    }
}