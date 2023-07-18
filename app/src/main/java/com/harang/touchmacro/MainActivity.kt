package com.harang.touchmacro

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.harang.touchmacro.provider.SharedPreferencesManager
import com.harang.touchmacro.ui.component.ServiceComposable
import com.harang.touchmacro.ui.overlay.OverlayScreen
import com.harang.touchmacro.ui.theme.TouchMacroTheme

class MainActivity : ComponentActivity() {
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
                        ServiceComposable()
                    }
                }
            }
        }
    }
}