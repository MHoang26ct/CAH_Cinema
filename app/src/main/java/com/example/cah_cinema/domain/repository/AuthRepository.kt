package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.domain.model.LoginResult

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    suspend fun register(email: String, password: String, name: String, phone: String?): Result<LoginResult>
    suspend fun logout(): Result<Unit>
    suspend fun changePassword(old: String, new: String): Result<Unit>
}
