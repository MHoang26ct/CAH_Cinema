package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.domain.model.User

interface UserRepository {
    suspend fun getMyProfile(): Result<User>
}
