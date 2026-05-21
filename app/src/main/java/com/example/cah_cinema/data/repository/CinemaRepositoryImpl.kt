package com.example.cah_cinema.data.repository

import com.example.cah_cinema.data.mapper.toDomain
import com.example.cah_cinema.data.model.BaseResponse
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.domain.repository.CinemaRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CinemaRepositoryImpl : CinemaRepository {
    private val gson = Gson()

    override suspend fun getCinemas(): Result<List<Cinema>> {
        return try {
            val response = RetrofitClient.apiService.getCinemas()
            if (response.isSuccessful) {
                val cinemas = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(cinemas)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string()) ?: "Lỗi: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCinemaDetail(id: Long): Result<Cinema> {
        return try {
            val response = RetrofitClient.apiService.getCinemaDetail(id)
            if (response.isSuccessful) {
                val cinema = response.body()?.data?.toDomain()
                if (cinema != null) {
                    Result.success(cinema)
                } else {
                    Result.failure(Exception("Không tìm thấy rạp"))
                }
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string()) ?: "Lỗi: ${response.code()}"
                Result.failure(Exception(errorMsg))
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
