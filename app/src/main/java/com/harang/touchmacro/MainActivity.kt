package com.harang.touchmacro

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
import com.harang.touchmacro.ui.component.ServiceComposable
import com.harang.touchmacro.ui.overlay.OverlayScreen
import com.harang.touchmacro.ui.theme.TouchMacroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val display: Display = windowManager.defaultDisplay
        val size: Point = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y
        Log.e("MainActivity", "width: $width, height: $height")
        setContent {
            TouchMacroTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics {
                            contentDescription = "MainActivity"
                        }
                        .clickable {

                    },
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