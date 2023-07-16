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
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.harang.touchmacro.service.OverlayService
import com.harang.touchmacro.view.CustomView
import com.harang.touchmacro.view.DownButton
import com.harang.touchmacro.view.LeftButton
import com.harang.touchmacro.view.RightButton
import com.harang.touchmacro.view.UpButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("ClickableViewAccessibility")
@Composable
fun OverlayScreen(
    changePosition: (Int, Int) -> Unit,
    swipe: () -> Unit,
    stopSelf: () -> Unit
) {
    val context = LocalContext.current
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
        .width(200.dp)
        .height(300.dp)
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
                    .width(100.dp)
                    .height(100.dp)
                    .background(
                        color = when(isCanvasShowing.value) {
                            true -> Color(0x55009688)
                            false -> Color(0xffFFC107)
                        }
                    )
                    .clickable {
                        Log.e("Canvas", "Canvas clicked")
                        Log.e("prevValue", isCanvasShowing.value.toString())
                        isCanvasShowing.value = !isCanvasShowing.value
                        Log.e("postValue", isCanvasShowing.value.toString())
                    }
            )
        }
    }
    if (isCanvasShowing.value) {
        Box(
            modifier = Modifier
                .width(1000.dp)
                .height(1000.dp)
                .background(
                    color = Color(0x55F50057)
                ),
        )
    }
}


//AndroidView(
//modifier = Modifier
//.width(IntrinsicSize.Min)
//.height(IntrinsicSize.Min),
//factory = { context ->
//    // Creates view
//    CustomView(context).apply {
//        // Sets up listeners for View -> Compose communication
//        setOnTouchListener { view, motionEvent ->
//            Log.e("CustomView", "View touched, ${view.width}, ${view.height}, $motionEvent")
//            coroutineScope2.launch {
//                delay(1000)
////                                view.dispatchTouchEvent(
////                                        MotionEvent.obtain(
////                                            SystemClock.uptimeMillis(),
////                                            SystemClock.uptimeMillis(),
////                                            MotionEvent.ACTION_DOWN,
////                                            800f,
////                                            800f,
////                                            0
////                                        )
////                                        )
////                                view.dispatchTouchEvent(
////                                    MotionEvent.obtain(
////                                        SystemClock.uptimeMillis(),
////                                        SystemClock.uptimeMillis(),
////                                        MotionEvent.ACTION_UP,
////                                        800f,
////                                        800f,
////                                        0
////                                    )
////                                )
//            }
//            false
//        }
//        setOnClickListener {view ->
////                            view.dispatchTouchEvent(
////                                MotionEvent.obtain(
////                                    SystemClock.uptimeMillis(),
////                                    SystemClock.uptimeMillis(),
////                                    MotionEvent.ACTION_DOWN,
////                                    -100f,
////                                    100f,
////                                    0
////                                )
////                            )
////                            view.dispatchTouchEvent(
////                                MotionEvent.obtain(
////                                    SystemClock.uptimeMillis(),
////                                    SystemClock.uptimeMillis(),
////                                    MotionEvent.ACTION_UP,
////                                    100f,
////                                    100f,
////                                    0
////                                )
////                            )
////                            coroutineScope2.launch {
////                                while(true) {
////                                    delay(1000)
////
////                                }
////                            }
//            Log.e("CustomView", "View clicked")
//        }
//    }
//},
//update = { view ->
//    // View's been inflated or state read in this block has been updated
//    // Add logic here if necessary
//
//    // As selectedItem is read here, AndroidView will recompose
//    // whenever the state changes
//    // Example of Compose -> View communication
//    Log.e("CustomView", "View updated")
//}
//)