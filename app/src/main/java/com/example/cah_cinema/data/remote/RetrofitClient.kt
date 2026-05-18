package com.example.cah_cinema.data.remote

import android.content.Context
import com.example.cah_cinema.util.PreferenceManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://100.89.144.114:8080/"

    private var token: String? = null
    private var preferenceManager: PreferenceManager? = null

    fun init(context: Context) {
        preferenceManager = PreferenceManager.getInstance(context)
        token = preferenceManager?.getToken()
    }

    fun setToken(newToken: String?) {
        token = newToken
        preferenceManager?.saveToken(newToken)
    }

    fun getToken(): String? = token

    fun saveAvatarUrl(url: String?) {
        preferenceManager?.saveAvatarUrl(url)
    }

    fun getLocalAvatarUrl(): String? = preferenceManager?.getAvatarUrl()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            token?.let {
                val bearerToken = if (it.startsWith("Bearer ")) it else "Bearer $it"
                requestBuilder.addHeader("Authorization", bearerToken)
            }
            chain.proceed(requestBuilder.build())
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
