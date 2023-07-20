package com.harang.touchassistant.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import android.os.Build
import android.view.WindowInsets
import android.view.accessibility.AccessibilityEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harang.touchassistant.R
import com.harang.touchassistant.data.GlobalConstants
import com.harang.touchassistant.vo.GlobalObject
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

                /*
                * Start Button / Pause Button
                * */
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(
                            color = Color(0xffEF9A9A)
                        )
                        .pointerInput(true) {
                            detectTapGestures(
                                onTap = {
//                                    Log.e("tap", "x: ${it.x}\ny: ${it.y}")
                                },
                                onDoubleTap = {
//                                    Log.e("double tap", "x: ${it.x}\ny: ${it.y}")
                                },
                                onLongPress = {
//                                    Log.e("long press", "x: ${it.x}\ny: ${it.y}")
                                },
                                onPress = {
//                                    Log.e("press", "x: ${it.x}\ny: ${it.y}")
                                    if (accessibilityManager.isEnabled) {
                                        val event =
                                            AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
                                        if (GlobalObject.isRunning) {
                                            GlobalObject.isRunning = false
                                            GlobalObject.isRunning_flow.update { false }
                                            event.text.add("Pause Touch Assistant")
                                        } else {
                                            GlobalObject.isRunning = true
                                            GlobalObject.isRunning_flow.update { true }
                                            event.text.add("Start Touch Assistant")
                                        }
                                        accessibilityManager.sendAccessibilityEvent(event)
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = when (GlobalObject.isRunning_flow.collectAsState().value) {
                                true -> R.drawable.baseline_pause_24
                                false -> R.drawable.baseline_play_arrow_24
                            }
                        ),
                        contentDescription = null
                    )
                }

                /*
                * Stop Button
                * */
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .background(
                            color = Color(0xffCE93D8)
                        )
                        .pointerInput(true) {
                            detectTapGestures(
                                onTap = {
//                                    Log.e("tap", "x: ${it.x}\ny: ${it.y}")
                                },
                                onDoubleTap = {
//                                    Log.e("double tap", "x: ${it.x}\ny: ${it.y}")
                                },
                                onLongPress = {
//                                    Log.e("long press", "x: ${it.x}\ny: ${it.y}")
                                },
                                onPress = {
//                                    Log.e("press", "x: ${it.x}\ny: ${it.y}")
                                    if (accessibilityManager.isEnabled) {
                                        GlobalObject.isRunning = false
                                        GlobalObject.isRunning_flow.update { false }
                                        GlobalObject.loopCount = 1
                                        GlobalObject.loopCount_flow.update { 1 }
                                        val event =
                                            AccessibilityEvent.obtain(AccessibilityEvent.TYPE_ANNOUNCEMENT)
                                        event.text.add("Stop Touch Assistant")
                                        accessibilityManager.sendAccessibilityEvent(event)
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = null
                    )
                }
            }
            Row() {

                /*
                * Stop Button
                * */
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
//                                    Log.e("tap", "x: ${it.x}\ny: ${it.y}")
                                },
                                onDoubleTap = {
//                                    Log.e("double tap", "x: ${it.x}\ny: ${it.y}")
                                },
                                onLongPress = {
//                                    Log.e("long press", "x: ${it.x}\ny: ${it.y}")
                                },
                                onPress = {
//                                    Log.e("press", "x: ${it.x}\ny: ${it.y}")
                                    GlobalObject.isRunning = false
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
                    Image(
                        painter = painterResource(id = R.drawable.baseline_stop_24),
                        contentDescription = null
                    )
                }

                /*
                * Current count display
                * */
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
                        text = "${GlobalObject.loopCount_flow.collectAsState().value}",
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