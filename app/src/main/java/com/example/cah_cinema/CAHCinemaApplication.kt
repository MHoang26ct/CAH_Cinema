package com.example.cah_cinema

import android.app.Application
import com.example.cah_cinema.data.remote.RetrofitClient

class CAHCinemaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}
