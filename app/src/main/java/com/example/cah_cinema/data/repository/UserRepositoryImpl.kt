package com.example.cah_cinema.data.repository

import com.example.cah_cinema.data.mapper.toDomain
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.model.User
import com.example.cah_cinema.domain.repository.UserRepository

class UserRepositoryImpl : UserRepository {
    override suspend fun getMyProfile(): Result<User> {
        return try {
            val response = RetrofitClient.apiService.getMyProfile()
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    Result.success(data.user.toDomain())
                } else {
                    Result.failure(Exception("Dữ liệu trống"))
                }
            } else {
                Result.failure(Exception("Lỗi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
