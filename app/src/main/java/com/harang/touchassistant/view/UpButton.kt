package com.harang.touchassistant.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.harang.touchassistant.R

class UpButton(context: Context) : LinearLayout(context) {

    init {
        init()
    }

    private fun init() {
        val view = LayoutInflater.from(context).inflate(R.layout.up_button, this, false)
        addView(view)
    }
}