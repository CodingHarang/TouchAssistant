package com.harang.touchmacro.ui.component

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.harang.touchmacro.service.OverlayService

@Composable
fun ServiceComposable() {
    val context = LocalContext.current
    context.startService(Intent(context, OverlayService::class.java))
}