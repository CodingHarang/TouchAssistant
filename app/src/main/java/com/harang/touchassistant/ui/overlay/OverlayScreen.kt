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
import android.os.Build
import android.view.WindowInsets
import android.view.accessibility.AccessibilityEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harang.touchassistant.R
import com.harang.touchassistant.data.GlobalConstants
import com.harang.touchassistant.data.InputType
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
    if (GlobalObject.isOverlayShowing.collectAsState().value) {
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
            .width(80.dp)
            .height(120.dp)
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
                            .width(40.dp)
                            .height(40.dp)
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
                            .width(40.dp)
                            .height(40.dp)
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
                                            GlobalObject.isRunning_flow.update {
                                                GlobalObject.isRunning = false
                                                false
                                            }
                                            GlobalObject.loopCount_flow.update {
                                                GlobalObject.loopCount = 1
                                                1
                                            }
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
                    * Add Input Button
                    * */
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                            .background(
                                color = Color(0xff9FA8DA)
                            )
                            .pointerInput(true) {
                                detectTapGestures(
                                    onTap = {
                                        Log.e("fullScreen", "fullScreen")
                                        updateIsFullScreen(true)
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
                            painter = painterResource(id = R.drawable.target_24),
                            contentDescription = null
                        )
                    }

                    /*
                    * Current count display
                    * */
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
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
                            text = "${GlobalObject.loopCount_flow.collectAsState().value}\n/${GlobalObject.gestureArrayList.size}",
                            fontSize = 12.sp
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
        if (GlobalObject.isFullScreenShowing_flow.collectAsState().value) {
            val isDotCreated = remember { mutableStateOf(false) }
            val inputType = remember { mutableStateOf(InputType.Touch) }
            val inputDataList = remember { mutableStateOf(listOf<IntOffset>()) }
//            val touchPoints = remember { mutableStateListOf<Offset>() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(true) {
                        detectTapGestures(
                            onTap = {
                                isDotCreated.value = true
                                inputType.value = InputType.Touch
                                Log.e("tap", "x: ${it.x}\ny: ${it.y}")
//                                updateIsFullScreen(false)
                                inputDataList.value = listOf(
                                    IntOffset(
                                        it.x.toInt(),
                                        it.y.toInt()
                                    )
                                )

                            },
                            onDoubleTap = {
                                Log.e("double tap", "x: ${it.x}\ny: ${it.y}")
                                updateIsFullScreen(false)
                            },
                            onLongPress = {
                                Log.e("long press", "x: ${it.x}\ny: ${it.y}")
                            },
                            onPress = {
                                Log.e("press", "x: ${it.x}\ny: ${it.y}")
                            }
                        )
                    }
                    .pointerInput(true) {
                        detectDragGestures(
                            onDragStart = {
                                isDotCreated.value = true
                                inputType.value = InputType.Drag
                                inputDataList.value = listOf(
                                    IntOffset(
                                        it.x.toInt(),
                                        it.y.toInt()
                                    )
                                )
                                Log.e("onDragStart", "x: ${it.x}\ny: ${it.y}")
                            },
                            onDrag = { change: PointerInputChange, dragAmount: Offset ->

                                inputDataList.value = inputDataList.value + listOf(
                                    IntOffset(
                                        change.position.x.toInt(),
                                        change.position.y.toInt()
                                    )
                                )
                                Log.e("onDrag", "x: ${change.position.x}\ny: ${change.position.y}\n${dragAmount.x}\n${dragAmount.y}")
                            },
                            onDragCancel = {
                                Log.e("onDragCancel", "Drag Canceled")
                            },
                            onDragEnd = {
                                Log.e("onDragEnd", "Drag Ended")
                            }
                        )
                    }
                    .background(
                        color = Color(0x55F50057)
                    ),
            ) {
                if (isDotCreated.value) {
                    if (inputType.value == InputType.Touch) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .offset {
                                    IntOffset(
                                        (inputDataList.value[0].x - (this.density * 20).toInt()),
                                        (inputDataList.value[0].y - (this.density * 20).toInt())
                                    )
                                }
                                .clip(
                                    CircleShape
                                )
                                .background(
                                    color = Color(0xFFA7FFEB),
                                    shape = CircleShape
                                )
                        )
                    } else {
                        for(i in 1 until inputDataList.value.size) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .offset {
                                        IntOffset(
                                            (inputDataList.value[i].x - (this.density * 20).toInt()),
                                            (inputDataList.value[i].y - (this.density * 20).toInt())
                                        )
                                    }
                                    .clip(
                                        CircleShape
                                    )
                                    .background(
                                        color = Color(0xFFA7FFEB),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
//            Canvas(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .pointerInput(true) {
//                        collectPointerInput(touchPoints)
//                    }
//                    .background(
//                        color = Color(0x55F50057)
//                    )
//            ) {
//                touchPoints.forEachIndexed { index, point ->
//                    if (index < touchPoints.size - 1) {
//                        val nextPoint = touchPoints[index + 1]
//                        drawLine(
//                            start = point,
//                            end = nextPoint,
//                            color = Color.Red,
//                            strokeWidth = 5f,
//                            cap = StrokeCap.Round
//                        )
//                    }
//                }
//            }
        }
    }
}

//suspend fun PointerInputScope.collectPointerInput(touchPoints: MutableList<Offset>) {
//    while (true) {
//        Log.e("collectPointerInput", "collectPointerInput")
//        val event = awaitPointerEventScope { awaitPointerEvent() }
//        Log.e("event", "${event.changes}")
//        val pointer = event.changes.firstOrNull()
//        when {
//            pointer?.pressed == true -> {
//                touchPoints.clear()
//                touchPoints.add(pointer.position)
//            }
//            pointer?.positionChanged() == true -> {
//                touchPoints.add(pointer.position)
//            }
//            pointer?.changedToUpIgnoreConsumed() == true -> {
//                // handle touch release if needed
//            }
//        }
//    }
//}