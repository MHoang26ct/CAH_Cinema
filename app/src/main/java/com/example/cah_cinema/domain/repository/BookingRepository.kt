package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.domain.model.Seat

interface BookingRepository {
    suspend fun getSeats(showtimeId: String): Result<List<Seat>>
}
