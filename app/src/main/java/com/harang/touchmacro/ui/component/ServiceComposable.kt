package com.harang.touchmacro.ui.component

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.harang.touchmacro.service.OverlayService

@Composable
fun ServiceComposable() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(200.dp)
            .background(
                color = Color(0xffE57373)
            )
            .clickable {
                context.startService(Intent(context, OverlayService::class.java))
            }
    )
}