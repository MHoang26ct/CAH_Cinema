package com.example.cah_cinema.data.repository

import com.example.cah_cinema.data.model.CreateBookingRequest
import com.example.cah_cinema.data.model.FoodOrderItemRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.model.Seat
import com.example.cah_cinema.domain.model.SeatStatus
import com.example.cah_cinema.domain.model.SeatType
import com.example.cah_cinema.domain.repository.BookingRepository

class BookingRepositoryImpl : BookingRepository {
    override suspend fun getSeats(showtimeId: Long): Result<List<Seat>> {
        return try {
            val response = RetrofitClient.apiService.getSeats(showtimeId)
            if (response.isSuccessful) {
                val seats = response.body()?.data?.map { item ->
                    Seat(
                        id = item.id.toString(),
                        row = item.rowLabel,
                        number = item.colLabel.padStart(2, '0'),
                        type = when(item.seatType.name) {
                            "VIP" -> SeatType.VIP
                            "COUPLE" -> SeatType.COUPLE
                            else -> SeatType.REGULAR
                        },
                        status = when(item.occupancyStatus) {
                            "AVAILABLE" -> SeatStatus.AVAILABLE
                            "LOCKED", "BOOKED", "SOLD" -> SeatStatus.TAKEN_BY_OTHERS
                            else -> SeatStatus.MAINTENANCE
                        }
                    )
                } ?: emptyList()
                Result.success(seats)
            } else {
                Result.failure(Exception("Lỗi tải sơ đồ ghế"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createBooking(
        showtimeId: Long,
        seatIds: List<Long>,
        paymentMethod: String,
        foodItems: List<Pair<Long, Int>>?
    ): Result<Long> {
        return try {
            val request = CreateBookingRequest(
                showtimeId = showtimeId,
                seatIds = seatIds,
                paymentMethod = paymentMethod,
                foodItems = foodItems?.map { FoodOrderItemRequest(it.first, it.second) }
            )
            val response = RetrofitClient.apiService.createBooking(request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.id)
            } else {
                Result.failure(Exception("Đặt vé thất bại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
