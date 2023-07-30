package com.harang.touchassistant.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings.Global
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.harang.touchassistant.data.GlobalConstants
import com.harang.touchassistant.provider.SharedPreferencesManager
import com.harang.touchassistant.ui.overlay.OverlayScreen
import com.harang.touchassistant.vo.GlobalObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.update
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
        SharedPreferencesManager.getInt("screenWidth"),
        SharedPreferencesManager.getInt("screenHeight") + SharedPreferencesManager.getInt("statusBarHeight"),
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
        if (event != null) {
            when (event.text.toString().replace("[", "").replace("]", "")) {
                "Start Touch Assistant" -> {

                    val executor = newSingleThreadContext("executor")
                    executor.executor.execute {
                        while (GlobalObject.isRunning) {
                            GlobalObject.gestureArrayList[GlobalObject.loopCount]()
                            GlobalObject.loopCount++
                            if (GlobalObject.loopCount >= GlobalObject.gestureArrayList.size) {
                                GlobalObject.loopCount = 1
                            }
                            GlobalObject.loopCount_flow.update { GlobalObject.loopCount }
                        }
                    }
                    executor.close()
                }

                "Pause Touch Assistant" -> {
                    Log.e("Pause", "Pause")
                }

                "Stop Touch Assistant" -> {
                    Log.e("Stop", "Stop")
                }

                "Right" -> {
                    Log.e("right", "right")
                }
            }
        }
    }

    private fun changePosition(x: Int, y: Int) {
        if (!GlobalObject.isFullScreenShowing) {
            params1.x += x
            params1.y += y
            Log.e("params1 position", "x: ${params1.x}\nY: ${params1.y}")
            windowManager.updateViewLayout(composeView, params1)
        }
    }

    private fun updateIsFullScreen(isFull: Boolean) {
        if (isFull) {
            GlobalObject.isFullScreenShowing_flow.update {
                GlobalObject.isFullScreenShowing = true
                true
            }
            windowManager.updateViewLayout(composeView, params2)
        } else {
            GlobalObject.isFullScreenShowing_flow.update {
                GlobalObject.isFullScreenShowing = false
                false
            }
            windowManager.updateViewLayout(composeView, params1)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.e("onServiceConnected", "onServiceConnected")
        makeGestureArray()
        Log.e("GestureArray", "${GlobalObject.gestureArrayList.size}")
        composeView = ComposeView(this)
        lifecycleOwner = MyLifecycleOwner()
        params1.gravity = Gravity.START or Gravity.TOP
        params1.x = 0
        params1.y = -(SharedPreferencesManager.getInt("statusBarHeight") + 1)
        params2.gravity = Gravity.START or Gravity.TOP
        params2.x = 0
        params2.y = -(SharedPreferencesManager.getInt("statusBarHeight") + 1)
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
        Log.e("dispatchTapGesture", "dispatchTapGesture")
        for (i in 1..repeat) {
            if (!GlobalObject.isRunning) break
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
            if (!GlobalObject.isRunning) break
            val swipePath = Path()
            swipePath.moveTo(moveToX, moveToY)
            swipePath.lineTo(lineToX, lineToY)
            val gestureBuilder = GestureDescription.Builder()
            gestureBuilder.addStroke(StrokeDescription(swipePath, 0, duration))
            dispatchGesture(gestureBuilder.build(), null, null)
            TimeUnit.MILLISECONDS.sleep(duration + delay)
        }
    }

    private fun makeGestureArray() {
        GlobalObject.gestureArrayList.clear()
        for (i in 1..15) {
            for (j in 1..6) {
                // use skill
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(420f, 1420f, 100, 100, 1) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(535f, 1400f, 100, 100, 1) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(180f, 1400f, 100, 100, 1) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(55f, 1400f, 100, 100, 1) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(300f, 1400f, 100, 100, 1) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(660f, 1400f, 100, 100, 1) })

                // tap cycle 2 times
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(355f, 500f, 20, 5, 50) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(355f, 1160f, 20, 5, 50) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(355f, 740f, 20, 5, 50) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(355f, 500f, 20, 5, 50) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(355f, 1160f, 20, 5, 50) })
                addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(355f, 740f, 20, 5, 50) })
            }
            // open warrior window
            addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(175f, 1525f, 200, 500, 1) })

            // slide down
            addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchDragGesture(355f, 1200f, 355f, 1400f, 500, 500, 1) })
            addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchDragGesture(355f, 1200f, 355f, 1400f, 500, 500, 1) })

            // 1st warrior upgrade
            addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 1200f, 50, 50, 5) })

            // 2nd warrior upgrade
            addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 1300f, 50, 50, 5) })

            // 3rd warrior upgrade
            addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 1400f, 50, 50, 5) })

            // close warrior window
            addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(175f, 1525f, 200, 500, 1) })
        }

        // prestige window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(50f, 1525f, 200, 500, 1) })

        // slide down
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchDragGesture(355f, 1200f, 355f, 1400f, 500, 500, 1) })

        // slide down
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchDragGesture(355f, 1200f, 355f, 1400f, 500, 500, 1) })

        // prestige
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 1250f, 200, 500, 1) })

        // prestige ok
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(355f, 1300f, 200, 15000, 1) })

        // close equipment full dialog
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(355f, 1030f, 200, 1000, 1) })

        // prestige window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(50f, 1525f, 200, 500, 1) })

        // wide window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(575f, 885f, 200, 1000, 1) })

        // tap level up
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(610f, 330f, 200, 500, 1) })

        // 1st skill upgrade - wide window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 700f, 200, 500, 1) })
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(460f, 700f, 200, 500, 1) })

        // 2nd skill upgrade - wide window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 820f, 200, 500, 1) })
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(460f, 820f, 200, 500, 1) })

        // 3rd skill upgrade - wide window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 940f, 200, 500, 1) })
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(460f, 940f, 200, 500, 1) })

        // 4th skill upgrade - wide window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 1060f, 200, 500, 1) })
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(460f, 1060f, 200, 500, 1) })

        // 5th skill upgrade - wide window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 1180f, 200, 500, 1) })
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(460f, 1180f, 200, 500, 1) })

        // 6th skill upgrade - wide window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 1300f, 200, 500, 1) })
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(460f, 1300f, 200, 500, 1) })

        // Mega Boost
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(600f, 1430f, 200, 500, 1) })
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(370f, 1130f, 200, 500, 1) })

        // wide window close
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(575f, 130f, 200, 1000, 1) })

        // artifact window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(540f, 1510f, 200, 500, 1) })

        // all artifact upgrade
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(590f, 1150f, 50, 50, 5) })

        // artifact window
        addGestureToArrayList(GlobalObject.gestureArrayList, { dispatchTapGesture(540f, 1510f, 200, 300, 1) })
    }

    private fun addGestureToArrayList(arrList: ArrayList<() -> Unit>, gesture: () -> Unit) {
        arrList.add(gesture)
    }
}