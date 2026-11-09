package com.example.taskmanager

import android.content.Context

/** Sesi login disimpan di SharedPreferences. */
class Session189(context: Context) {
    private val prefs = context.getSharedPreferences("session_189", Context.MODE_PRIVATE)

    fun save(user: User189) {
        prefs.edit().putInt("user_id", user.id).putString("username", user.username).apply()
    }

    fun userId(): Int = prefs.getInt("user_id", -1)
    fun username(): String = prefs.getString("username", "") ?: ""
    fun isLoggedIn(): Boolean = userId() != -1
    fun logout() = prefs.edit().clear().apply()
}
