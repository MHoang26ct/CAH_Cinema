package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.domain.model.Cinema

interface CinemaRepository {
    suspend fun getCinemas(): Result<List<Cinema>>
    suspend fun getCinemaDetail(id: Long): Result<Cinema>
}
