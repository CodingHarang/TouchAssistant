package com.harang.touchassistant.provider

import android.content.Context
import android.content.SharedPreferences
import com.harang.touchassistant.TouchAssistantApplication
import com.harang.touchassistant.data.GlobalConstants

object SharedPreferencesManager {
    private val pref: SharedPreferences = TouchAssistantApplication.applicationContext().getSharedPreferences(GlobalConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    private fun putStringAndCommit(key: String, string: String) {
        editor.putString(key, string)
        editor.commit()
    }

    private fun getStringAndReturn(key: String): String {
        return pref.getString(key, "") ?: ""
    }

    private fun putIntAndCommit(key: String, int: Int) {
        editor.putInt(key, int)
        editor.commit()
    }

    private fun getIntAndReturn(key: String): Int {
        return pref.getInt(key, 0)
    }

    fun putString(key: String, value: String) {
        putStringAndCommit(key, value)
    }

    fun getString(key: String): String {
        return getStringAndReturn(key)
    }

    fun putInt(key: String, value: Int) {
        putIntAndCommit(key, value)
    }

    fun getInt(key: String): Int {
        return getIntAndReturn(key)
    }
}