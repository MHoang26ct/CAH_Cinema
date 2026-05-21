package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.domain.model.Seat

interface BookingRepository {
    suspend fun getSeats(showtimeId: Long): Result<List<Seat>>
    suspend fun createBooking(showtimeId: Long, seatIds: List<Long>, paymentMethod: String, foodItems: List<Pair<Long, Int>>? = null): Result<Long>
}
