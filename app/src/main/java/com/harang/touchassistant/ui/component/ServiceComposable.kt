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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.harang.touchassistant.provider.SharedPreferencesManager
import com.harang.touchassistant.view.DownButton
import com.harang.touchassistant.view.LeftButton
import com.harang.touchassistant.view.RightButton
import com.harang.touchassistant.view.UpButton
import com.harang.touchassistant.vo.GlobalObject

@Composable
fun ServiceComposable() {
    val str = remember { mutableStateOf("") }
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
        TextField(
            value = str.value,
            onValueChange = {
                str.value = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        Box(
            modifier = Modifier
                .padding(40.dp)
                .width(100.dp)
                .height(100.dp)
                .background(
                    color = Color(0xFFEA80FC)
                )
                .clickable {
                    SharedPreferencesManager.putInt("loop_count", str.value.toInt())
                }
        ) {
            Text(
                text = "Save",
                fontSize = 30.sp
            )
        }
    }
}