package com.example.cah_cinema

import android.app.Application
import com.example.cah_cinema.data.local.TokenManager
import com.example.cah_cinema.data.remote.RetrofitClient

class CAHCinemaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Nạp lại token từ SharedPreferences khi khởi động app
        val tokenManager = TokenManager(this)
        RetrofitClient.setToken(tokenManager.getToken())
    }
}
