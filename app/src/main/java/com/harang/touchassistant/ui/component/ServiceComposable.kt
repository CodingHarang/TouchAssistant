package com.harang.touchassistant.ui.component

import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.harang.touchassistant.view.DownButton
import com.harang.touchassistant.view.LeftButton
import com.harang.touchassistant.view.RightButton
import com.harang.touchassistant.view.UpButton

@Composable
fun ServiceComposable(
    foregroundStartService: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .semantics {
                contentDescription = "ServiceComposable"
            }
    ) {
        Button(
            onClick = {
                foregroundStartService("Start")
            },
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Text(
                text = "Trigger"
            )
        }
        Button(
            onClick = {
                foregroundStartService("Exit")
            },
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Text(
                text = "Exit"
            )
        }
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

        val view = LocalView.current
        val accessibilityManager = view.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .background(
                    color = Color(0xffE57373)
                )
                .clickable(
                    onClickLabel = "pink Box touched"
                ) {
                    if (accessibilityManager.isEnabled) {
                        val event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
                        event.text.add("Your announcement text")
                        accessibilityManager.sendAccessibilityEvent(event)
                    }
                    Log.e("button", "pink Box touched")
                }
        ) {
        }
        Button(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
                .background(
                    color = Color(0xffE57373)
                ),
            onClick = {
                Log.e("button", "pink button clicked")
            }
        ) {

        }

        Checkbox(
            checked = false,
            onCheckedChange = {}
        )
    }
}