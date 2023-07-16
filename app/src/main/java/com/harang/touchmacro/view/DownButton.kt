package com.harang.touchmacro.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.harang.touchmacro.R

class DownButton(context: Context) : LinearLayout(context) {

    init {
        init()
    }

    private fun init() {
        val view = LayoutInflater.from(context).inflate(R.layout.down_button, this, false)
        addView(view)
    }
}