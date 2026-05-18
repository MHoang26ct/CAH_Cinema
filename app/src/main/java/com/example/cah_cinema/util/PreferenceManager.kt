package com.example.cah_cinema.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cah_cinema_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String?) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun saveAvatarUrl(url: String?) {
        prefs.edit().putString("avatar_url", url).apply()
    }

    fun getAvatarUrl(): String? {
        return prefs.getString("avatar_url", null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private var instance: PreferenceManager? = null

        fun getInstance(context: Context): PreferenceManager {
            if (instance == null) {
                instance = PreferenceManager(context.applicationContext)
            }
            return instance!!
        }
    }
}
