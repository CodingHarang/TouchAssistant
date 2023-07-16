package com.harang.touchmacro.ui.component

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.harang.touchmacro.service.MyAccessibilityService
import com.harang.touchmacro.service.OverlayService
import com.harang.touchmacro.view.CustomView
import com.harang.touchmacro.view.DownButton
import com.harang.touchmacro.view.LeftButton
import com.harang.touchmacro.view.RightButton
import com.harang.touchmacro.view.UpButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ServiceComposable() {
    val context = LocalContext.current
    Column() {
        Row() {
            AndroidView(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min),
                factory = { context ->
                    UpButton(context).apply {
                        setOnClickListener {view ->
                            Log.e("UpButton", "UpButton clicked")
                        }
                    }
                },
                update = { view ->
                }
            )
            AndroidView(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min),
                factory = { context ->
                    DownButton(context).apply {
                        setOnClickListener {view ->
                            Log.e("DownButton", "DownButton clicked")
                        }
                    }
                },
                update = { view ->
                }
            )
        }
        Row() {
            AndroidView(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min),
                factory = { context ->
                    LeftButton(context).apply {
                        setOnClickListener {view ->
                            Log.e("LeftButton", "LeftButton clicked")
                        }
                    }
                },
                update = { view ->
                }
            )
            AndroidView(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min),
                factory = { context ->
                    RightButton(context).apply {
                        setOnClickListener {view ->
                            Log.e("RightButton", "RightButton clicked")
                        }
                    }
                },
                update = { view ->
                }
            )
        }

        Box(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .background(
                    color = Color(0xffE57373)
                )
                .clickable {
                    Log.e("button", "pink button touched")
                }
        )
    }
}