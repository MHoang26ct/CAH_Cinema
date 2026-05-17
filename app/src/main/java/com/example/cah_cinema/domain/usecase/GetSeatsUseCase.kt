package com.example.cah_cinema.domain.usecase

import com.example.cah_cinema.domain.model.Seat
import com.example.cah_cinema.domain.repository.BookingRepository

class GetSeatsUseCase(private val repository: BookingRepository) {
    suspend operator fun invoke(showtimeId: String): Result<List<Seat>> {
        // Backend sẽ thực hiện logic lấy data từ API trong Repository implementation
        return repository.getSeats(showtimeId)
    }
}
