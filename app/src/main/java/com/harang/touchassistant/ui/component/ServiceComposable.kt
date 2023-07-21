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
import androidx.compose.foundation.layout.padding
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
import com.harang.touchassistant.vo.GlobalObject

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
        Box(
            modifier = Modifier
                .padding(40.dp)
                .width(100.dp)
                .height(100.dp)
                .background(
                    color = Color(0xFFEA80FC)
                )
                .clickable {
                    GlobalObject.isOverlayShowing.value = !GlobalObject.isOverlayShowing.value
                }
        ) {
            Text(
                text = "Start"
            )
        }
    }
}