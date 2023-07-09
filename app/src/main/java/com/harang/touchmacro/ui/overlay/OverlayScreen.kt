package com.harang.touchmacro.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.harang.touchmacro.service.OverlayService
import com.harang.touchmacro.view.CustomView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("ClickableViewAccessibility")
@Composable
fun OverlayScreen(
    changePosition: (Int, Int) -> Unit,
    stopSelf: () -> Unit
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val interactions = remember { mutableStateListOf<Interaction>() }
    val accessibilityManager: AccessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

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
    val coroutineScope2 = rememberCoroutineScope()
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val modifier = Modifier
        .offset {
            IntOffset(
                x = offsetX.toInt(),
                y = offsetY.toInt()
            )
        }
        .width(600.dp)
        .height(600.dp)
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
            color = Color(0xff0000ff),
            shape = RoundedCornerShape(8.dp)
        )

    Column(
        modifier = modifier
    ) {
        AndroidView(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min),
            factory = { context ->
                // Creates view
                CustomView(context).apply {
                    // Sets up listeners for View -> Compose communication
                    setOnTouchListener { view, motionEvent ->
                        Log.e("CustomView", "View touched, ${view.width}, ${view.height}, $motionEvent")
                        coroutineScope2.launch {
                            delay(1000)
//                                view.dispatchTouchEvent(
//                                        MotionEvent.obtain(
//                                            SystemClock.uptimeMillis(),
//                                            SystemClock.uptimeMillis(),
//                                            MotionEvent.ACTION_DOWN,
//                                            800f,
//                                            800f,
//                                            0
//                                        )
//                                        )
//                                view.dispatchTouchEvent(
//                                    MotionEvent.obtain(
//                                        SystemClock.uptimeMillis(),
//                                        SystemClock.uptimeMillis(),
//                                        MotionEvent.ACTION_UP,
//                                        800f,
//                                        800f,
//                                        0
//                                    )
//                                )
                        }
                        false
                    }
                    setOnClickListener {view ->
//                            view.dispatchTouchEvent(
//                                MotionEvent.obtain(
//                                    SystemClock.uptimeMillis(),
//                                    SystemClock.uptimeMillis(),
//                                    MotionEvent.ACTION_DOWN,
//                                    -100f,
//                                    100f,
//                                    0
//                                )
//                            )
//                            view.dispatchTouchEvent(
//                                MotionEvent.obtain(
//                                    SystemClock.uptimeMillis(),
//                                    SystemClock.uptimeMillis(),
//                                    MotionEvent.ACTION_UP,
//                                    100f,
//                                    100f,
//                                    0
//                                )
//                            )
//                            coroutineScope2.launch {
//                                while(true) {
//                                    delay(1000)
//
//                                }
//                            }
                        Log.e("CustomView", "View clicked")
                    }
                }
            },
            update = { view ->
                // View's been inflated or state read in this block has been updated
                // Add logic here if necessary

                // As selectedItem is read here, AndroidView will recompose
                // whenever the state changes
                // Example of Compose -> View communication
                Log.e("CustomView", "View updated")
            }
        )
        Text(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = Color(0xff00ff00),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    Log.e("GreenButton", "green button clicked")
                    Log.e("GreenButton", "${accessibilityManager.isEnabled}")
//                    if (accessibilityManager.isEnabled) {
//                        val event: AccessibilityEvent = AccessibilityEvent.obtain()
//                        event.eventType = AccessibilityEvent.TYPE_VIEW_CLICKED
//                        event.className = Button::class.java.name
//                        accessibilityManager.sendAccessibilityEvent(event)
//                    }
                },
            text = "Button"
        )
        Text(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(
                    shape = RoundedCornerShape(8.dp)
                )
                .background(
                    color = Color(0xff00ff00),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    stopSelf()
                    context.stopService(Intent(context, OverlayService::class.java))
                },
            text = "Stop"
        )
    }
}