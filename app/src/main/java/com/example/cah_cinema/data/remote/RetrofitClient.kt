package com.example.cah_cinema.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val TAG = "RetrofitClient"
    private const val BASE_URL = "http://100.89.144.114:8080/"

    @Volatile
    private var token: String? = null

    /**
     * Cập nhật token cho các request.
     * Note: Mặc dù api-docs nói không cần Bearer, nhưng mã nguồn Backend 
     * (JwtAuthenticationFilter.java) bắt buộc phải có tiền tố "Bearer ".
     */
    fun setToken(newToken: String?) {
        token = newToken
        Log.d(TAG, "Token updated. Length: ${newToken?.length ?: 0}")
    }

    fun getToken(): String? = token

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            
            // Không gửi Token cho các endpoint xác thực (Login/Register)
            val path = original.url.encodedPath
            val isAuthEndpoint = path.contains("/api/v1/auth/login") || 
                               path.contains("/api/v1/auth/register") ||
                               path.contains("/api/v1/auth/send-otp")
            
            val currentToken = token
            if (!currentToken.isNullOrBlank() && !isAuthEndpoint) {
                // Thêm tiền tố Bearer theo yêu cầu thực tế của Backend
                val fullToken = if (currentToken.startsWith("Bearer ")) currentToken else "Bearer $currentToken"
                requestBuilder.header("Authorization", fullToken)
                Log.d(TAG, "Adding Authorization header to: $path")
            } else {
                Log.d(TAG, "Skipping Authorization header for: $path")
            }
            
            val request = requestBuilder.build()
            val response = chain.proceed(request)
            
            // Xử lý lỗi 401 tập trung
            if (response.code == 401) {
                Log.w(TAG, "Received 401 Unauthorized from: $path. Clearing token.")
                token = null
            }
            
            response
        }
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
