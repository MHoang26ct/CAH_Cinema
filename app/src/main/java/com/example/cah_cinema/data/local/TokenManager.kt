package com.example.cah_cinema.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cah_cinema_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String?) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun clearToken() {
        prefs.edit().remove("auth_token").apply()
    }
}
