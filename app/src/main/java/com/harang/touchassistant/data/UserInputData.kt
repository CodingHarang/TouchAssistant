package com.harang.touchassistant.data

data class UserInputData(
    var moveToX: Float,
    var moveToY: Float,
    var lineToX: Float,
    var lineToY: Float,
    var type: InputType
)

enum class InputType {
    Touch, Drag
}