package com.harang.touchmacro

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.harang.touchmacro.provider.SharedPreferencesManager
import com.harang.touchmacro.ui.component.ServiceComposable
import com.harang.touchmacro.ui.theme.TouchMacroTheme
import com.harang.touchmacro.utils.foregroundStartService

class MainActivity : ComponentActivity() {

    lateinit var mpm: MediaProjectionManager
    lateinit var mediaProjection: MediaProjection
    lateinit var virtualDisplay: VirtualDisplay

    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemNavigationBarResourceId = applicationContext.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val systemStatusBarResourceId = applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android")
        SharedPreferencesManager.putInt("screen_width", resources.displayMetrics.widthPixels)

        if (systemNavigationBarResourceId > 0) {
            if (systemStatusBarResourceId > 0) {
                val navigationBarHeight = applicationContext.resources.getDimensionPixelSize(systemNavigationBarResourceId)
                val statusBarHeight = applicationContext.resources.getDimensionPixelSize(systemStatusBarResourceId)
                SharedPreferencesManager.putInt("screen_height", resources.displayMetrics.heightPixels + navigationBarHeight)
                SharedPreferencesManager.putInt("status_bar_height", statusBarHeight)
            } else {
                val navigationBarHeight = applicationContext.resources.getDimensionPixelSize(systemNavigationBarResourceId)
                SharedPreferencesManager.putInt("screen_height", resources.displayMetrics.heightPixels + navigationBarHeight)
            }
        } else {
            if (systemStatusBarResourceId > 0) {
                val statusBarHeight = applicationContext.resources.getDimensionPixelSize(systemStatusBarResourceId)
                SharedPreferencesManager.putInt("screen_height", resources.displayMetrics.heightPixels)
                SharedPreferencesManager.putInt("status_bar_height", statusBarHeight)
            } else {
                SharedPreferencesManager.putInt("screen_height", resources.displayMetrics.heightPixels)
            }
        }
        Log.e("size", "${SharedPreferencesManager.getInt("screen_width")}, ${SharedPreferencesManager.getInt("screen_height")}")

//        val mediaProjectionManager = getSystemService(MediaProjectionManager::class.java)
//        var mediaProjection : MediaProjection
//
//        val startMediaProjection = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == RESULT_OK) {
//                mediaProjection = mediaProjectionManager
//                    .getMediaProjection(result.resultCode, result.data!!)
//
//                virtualDisplay = mediaProjection.createVirtualDisplay(
//                    "ScreenCapture",
//                    500,
//                    500,
//                    resources.displayMetrics.densityDpi,
//                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
//                    null,
//                    null, null)
//            }
//        }

//        startMediaProjection.launch(mediaProjectionManager.createScreenCaptureIntent())

        setContent {
            TouchMacroTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        ServiceComposable(
                            foregroundStartService = { foregroundStartService(it) }
                        )
                    }
                }
            }
        }
    }
}