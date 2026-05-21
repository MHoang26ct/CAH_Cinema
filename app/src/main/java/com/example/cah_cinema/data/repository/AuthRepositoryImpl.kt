package com.example.cah_cinema.data.repository

import com.example.cah_cinema.data.mapper.toDomain
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.model.LoginResult
import com.example.cah_cinema.domain.repository.AuthRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AuthRepositoryImpl : AuthRepository {
    private val gson = Gson()

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val response = RetrofitClient.apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string()) ?: "Đăng nhập thất bại"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String, name: String, phone: String?): Result<LoginResult> {
        return try {
            val response = RetrofitClient.apiService.register(RegisterRequest(email, password, name, phone))
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.toDomain())
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string()) ?: "Đăng ký thất bại"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            val token = RetrofitClient.getToken() ?: return Result.success(Unit)
            val response = RetrofitClient.apiService.logout(RefreshTokenRequest(token))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Đăng xuất thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(old: String, new: String): Result<Unit> {
        return try {
            val response = RetrofitClient.apiService.changePassword(ChangePasswordRequest(old, new))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Đổi mật khẩu thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            val type = object : TypeToken<BaseResponse<Unit>>() {}.type
            val errorResponse: BaseResponse<Unit> = gson.fromJson(errorBody, type)
            errorResponse.message
        } catch (e: Exception) {
            null
        }
    }
}
