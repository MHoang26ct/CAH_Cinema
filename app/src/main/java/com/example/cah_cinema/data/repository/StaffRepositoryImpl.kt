package com.example.cah_cinema.data.repository

import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.repository.StaffRepository

class StaffRepositoryImpl : StaffRepository {

    private val api = RetrofitClient.apiService

    override suspend fun checkIn(qrToken: String): Result<CheckInResponse> = runCatching {
        val response = api.checkInTicket(CheckInRequest(qrToken = qrToken))
        if (response.isSuccessful) {
            response.body()?.data
                ?: throw Exception("Không có dữ liệu trả về")
        } else {
            val errorBody = response.errorBody()?.string() ?: "Lỗi không xác định"
            val message = try {
                val json = com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                json.get("message")?.asString ?: errorBody
            } catch (e: Exception) {
                errorBody
            }
            throw Exception(message)
        }
    }

    override suspend fun getSeatsByShowtime(showtimeId: Long): Result<List<SeatItem>> = runCatching {
        val response = api.getStaffSeats(showtimeId)
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception("Không thể tải danh sách ghế")
        }
    }

    override suspend fun createCashBooking(
        showtimeId: Long,
        seatIds: List<Long>
    ): Result<BookingData> = runCatching {
        val request = StaffCreateBookingRequest(
            showtimeId = showtimeId,
            seatIds = seatIds,
            paymentMethod = "CASH"
        )
        val response = api.staffCreateBooking(request)
        if (response.isSuccessful) {
            response.body()?.data ?: throw Exception("Không có dữ liệu booking")
        } else {
            val errorBody = response.errorBody()?.string() ?: ""
            val message = try {
                com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                    .get("message")?.asString ?: "Tạo booking thất bại"
            } catch (e: Exception) {
                "Tạo booking thất bại"
            }
            throw Exception(message)
        }
    }

    override suspend fun confirmCashPayment(bookingId: Long): Result<Unit> = runCatching {
        // Sử dụng paymentRef duy nhất cho mỗi booking để tránh trùng lặp UNIQUE constraint
        val request = ConfirmPaymentRequest(
            paymentRef = "CASH_$bookingId",
            gateway = "CASH"
        )
        val response = api.confirmPaymentManual(bookingId, request)
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string() ?: ""
            val message = try {
                com.google.gson.JsonParser.parseString(errorBody).asJsonObject
                    .get("message")?.asString ?: "Xác nhận thanh toán thất bại"
            } catch (e: Exception) {
                "Xác nhận thanh toán thất bại"
            }
            throw Exception(message)
        }
    }

    override suspend fun getCinemas(): Result<List<CinemaItem>> = runCatching {
        val response = api.getCinemas()
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception("Không thể tải danh sách rạp")
        }
    }

    override suspend fun getShowtimesByCinema(cinemaId: Long, date: String): Result<List<CinemaShowtimeItem>> = runCatching {
        val response = api.getShowtimesByCinema(cinemaId, date)
        if (response.isSuccessful) {
            response.body()?.data ?: emptyList()
        } else {
            throw Exception("Không thể tải lịch chiếu")
        }
    }
}
