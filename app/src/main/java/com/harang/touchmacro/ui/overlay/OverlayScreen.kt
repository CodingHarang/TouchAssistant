package com.harang.touchmacro.ui.overlay

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import android.graphics.Path
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.harang.touchmacro.data.GlobalConstants
import com.harang.touchmacro.service.OverlayService
import com.harang.touchmacro.view.CustomView
import com.harang.touchmacro.view.DownButton
import com.harang.touchmacro.view.LeftButton
import com.harang.touchmacro.view.RightButton
import com.harang.touchmacro.view.UpButton
import com.harang.touchmacro.vo.GlobalObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@SuppressLint("ClickableViewAccessibility")
@Composable
fun OverlayScreen(
    changePosition: (Int, Int) -> Unit,
    updateIsFullScreen: (Boolean) -> Unit,
    swipe: () -> Unit,
    stopSelf: () -> Unit
) {
    val isFullScreen = remember { mutableStateOf(GlobalObject.isFullScreenShowing) }
    val view = LocalView.current
    val accessibilityManager = view.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        view.windowInsetsController?.hide(WindowInsets.Type.statusBars())
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isCanvasShowing = remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is DragInteraction.Start -> {
                    Log.e("Drag", "Start")
                }
                is DragInteraction.Stop -> {
                    Log.e("Drag", "End")
                }
                is DragInteraction.Cancel -> {
                    Log.e("Drag", "Cancel")
                }
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val modifier = Modifier
        .offset {
            IntOffset(
                x = offsetX.toInt(),
                y = offsetY.toInt()
            )
        }
        .width(100.dp)
        .height(100.dp)
        .pointerInput(true) {
            var interaction: DragInteraction.Start? = null
            detectDragGestures(
                onDragStart = {
                    coroutineScope.launch {
                        interaction = DragInteraction.Start()
                        interaction?.run {
                            interactionSource.emit(this)
                        }
                    }
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    changePosition(dragAmount.x.toInt(), dragAmount.y.toInt())
//                    offsetX += dragAmount.x
//                    offsetY += dragAmount.y
                },
                onDragCancel = {
                    coroutineScope.launch {
                        interaction?.run {
                            interactionSource.emit(DragInteraction.Cancel(this))
                        }
                    }
                },
                onDragEnd = {
                    coroutineScope.launch {
                        interaction?.run {
                            interactionSource.emit(DragInteraction.Stop(this))
                        }
                    }
                }
            )
        }
        .background(
            color = Color(0xffffffff),
            shape = RoundedCornerShape(8.dp)
        )
    Box(
    ) {
        Column(
            modifier = modifier
        ) {
            Row() {
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(
                            color = Color(0xffEF9A9A)
                        )
                        .clickable {
                            if (accessibilityManager.isEnabled) {
                                val event =
                                    AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
                                event.text.add("Up")
                                accessibilityManager.sendAccessibilityEvent(event)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Up",
                        fontSize = 30.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(
                            color = Color(0xffCE93D8)
                        )
                        .clickable {
                            if (accessibilityManager.isEnabled) {
                                val event =
                                    AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
                                event.text.add("Down")
                                accessibilityManager.sendAccessibilityEvent(event)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Down",
                        fontSize = 30.sp
                    )
                }
            }
            Row() {
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(
                            color = Color(0xff9FA8DA)
                        )
                        .pointerInput(true) {
                            detectTapGestures(
                                onTap = {
                                    Log.e("tap", "x: ${it.x}\ny: ${it.y}")
                                },
                                onDoubleTap = {
                                    Log.e("double tap", "x: ${it.x}\ny: ${it.y}")
                                },
                                onLongPress = {
                                    Log.e("long press", "x: ${it.x}\ny: ${it.y}")
                                },
                                onPress = {
                                    Log.e("press", "x: ${it.x}\ny: ${it.y}")
                                    GlobalConstants.isLooping = false
                                }
                            )
                        },
//                        .clickable {
//                            if (accessibilityManager.isEnabled) {
//                                val event =
//                                    AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
//                                event.text.add("Left")
//                                accessibilityManager.sendAccessibilityEvent(event)
//                            }
//                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Left",
                        fontSize = 30.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(
                            color = Color(0xff81D4FA)
                        )
                        .clickable {
                            if (accessibilityManager.isEnabled) {
                                val event =
                                    AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
                                event.text.add("Right")
                                accessibilityManager.sendAccessibilityEvent(event)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Right",
                        fontSize = 30.sp
                    )
                }
            }
//            Box(
//                modifier = Modifier
//                    .width(50.dp)
//                    .height(50.dp)
//                    .background(
//                        color = Color(0xff80CBC4)
//                    )
//                    .clickable {
//                        if (GlobalObject.isFullScreenShowing) {
//                            GlobalObject.isFullScreenShowing = false
//                            updateIsFullScreen(GlobalObject.isFullScreenShowing)
//                        } else {
//                            GlobalObject.isFullScreenShowing = true
//                            updateIsFullScreen(GlobalObject.isFullScreenShowing)
//                        }
//                        isFullScreen.value = GlobalObject.isFullScreenShowing
//                        Log.e(
//                            "GlobalObject.isFullScreenShowing",
//                            GlobalObject.isFullScreenShowing.toString()
//                        )
//                    }
//            ) {
//                Text(
//                    text = "FullScreen",
//                    fontSize = 30.sp
//                )
//            }
        }
    }
    if (isFullScreen.value) {
//        Log.e("isFullScreenShowing", GlobalObject.isFullScreenShowing.toString())
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .pointerInput(true) {
//                    detectTapGestures(
//                        onTap = {
//                            Log.e("tap", "x: ${it.x}\ny: ${it.y}")
//                        },
//                        onDoubleTap = {
//                            Log.e("double tap", "x: ${it.x}\ny: ${it.y}")
//                        },
//                        onLongPress = {
//                            Log.e("long press", "x: ${it.x}\ny: ${it.y}")
//                        },
//                        onPress = {
//                            Log.e("press", "x: ${it.x}\ny: ${it.y}")
//                        }
//                    )
//                }
//                .pointerInput(true) {
//                    detectDragGestures (
//                        onDragStart = {
//                            Log.e("onDragStart", "x: ${it.x}\ny: ${it.y}")
//                        },
//                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
//                            Log.e("onDrag", "x: ${change.position.x}\ny: ${change.position.y}")
//                        },
//                        onDragCancel = {
//                            Log.e("onDragCancel", "Drag Canceled")
//                        },
//                        onDragEnd = {
//                            Log.e("onDragEnd", "Drag Ended")
//                        }
//                    )
//                }
//                .background(
//                    color = Color(0x55F50057)
//                ),
//        )
    }
}