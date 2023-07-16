package com.harang.touchmacro.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.harang.touchmacro.ui.overlay.OverlayScreen


class OverlayService : Service() {

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

    private lateinit var composeView: ComposeView
    private lateinit var lifecycleOwner: MyLifecycleOwner

    override fun onCreate() {
        super.onCreate()
//        setTheme(R.style.ThemeOverlay_AppCompat_Light)
        composeView = ComposeView(this)
        lifecycleOwner = MyLifecycleOwner()
        showOverlay(
            stopSelf = {
                lifecycleOwner.setCurrentState(Lifecycle.State.DESTROYED)
                stopSelf()
            }
        )
    }

    fun changePosition(x: Int, y: Int) {
        params.x += x
        params.y += y
        windowManager.updateViewLayout(composeView, params)
    }

    private fun showOverlay(
        stopSelf: () -> Unit
    ) {
        composeView.setContent {
            OverlayScreen(
                changePosition = { x: Int, y: Int ->
                    changePosition(x, y)
                },
                swipe = {},
                stopSelf = stopSelf
            )
//            Text(
//                text = "Hello",
//                color = Color.Black,
//                fontSize = 50.sp,
//                modifier = Modifier
//                    .wrapContentSize()
//                    .background(Color.Green)
//            )
        }

        // Trick The ComposeView into thinking we are tracking lifecycle
        val viewModelStore = ViewModelStore()
        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        windowManager.addView(composeView, params)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.e("OverlayService", "onDestroy")
//        composeView.viewtree
        super.onDestroy()
    }
}