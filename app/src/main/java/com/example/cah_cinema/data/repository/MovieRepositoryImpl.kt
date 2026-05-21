package com.example.cah_cinema.data.repository

import com.example.cah_cinema.data.mapper.toDomainMovie
import com.example.cah_cinema.data.model.BaseResponse
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.model.FeaturedMovies
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.repository.MovieRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MovieRepositoryImpl : MovieRepository {
    private val gson = Gson()

    override suspend fun getFeaturedMovies(): Result<FeaturedMovies> {
        return try {
            val response = RetrofitClient.apiService.getFeaturedMovies()
            if (response.isSuccessful) {
                val data = response.body()?.data
                if (data != null) {
                    Result.success(
                        FeaturedMovies(
                            nowShowing = data.nowShowing.map { it.toDomainMovie() },
                            upcoming = data.upcoming.map { it.toDomainMovie() }
                        )
                    )
                } else {
                    Result.failure(Exception("Dữ liệu trống"))
                }
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string()) ?: "Lỗi: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovies(): Result<List<Movie>> {
        return try {
            val response = RetrofitClient.apiService.getMovies()
            if (response.isSuccessful) {
                val movies = response.body()?.data?.content?.map { it.toDomainMovie() } ?: emptyList()
                Result.success(movies)
            } else {
                val errorMsg = parseErrorMessage(response.errorBody()?.string()) ?: "Lỗi: ${response.code()}"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMovieDetail(id: Long): Result<Movie> {
        return try {
            val response = RetrofitClient.apiService.getMovieDetail(id)
            if (response.isSuccessful) {
                val movie = response.body()?.data?.toDomainMovie()
                if (movie != null) {
                    Result.success(movie)
                } else {
                    Result.failure(Exception("Không tìm thấy phim"))
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
