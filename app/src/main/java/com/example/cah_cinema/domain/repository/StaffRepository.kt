package com.example.cah_cinema.domain.repository

import com.example.cah_cinema.data.model.BookingData
import com.example.cah_cinema.data.model.CheckInResponse
import com.example.cah_cinema.data.model.CinemaItem
import com.example.cah_cinema.data.model.CinemaShowtimeItem
import com.example.cah_cinema.data.model.SeatItem

interface StaffRepository {
    /** Quét QR code để check-in vé */
    suspend fun checkIn(qrToken: String): Result<CheckInResponse>

    /** Lấy danh sách ghế theo suất chiếu để bán tại quầy */
    suspend fun getSeatsByShowtime(showtimeId: Long): Result<List<SeatItem>>

    /** Tạo booking tại quầy (CASH) */
    suspend fun createCashBooking(
        showtimeId: Long,
        seatIds: List<Long>
    ): Result<BookingData>

    /** Xác nhận thanh toán tiền mặt sau khi nhận tiền từ khách */
    suspend fun confirmCashPayment(bookingId: Long): Result<Unit>

    /** Lấy danh sách rạp (Public) */
    suspend fun getCinemas(): Result<List<CinemaItem>>

    /** Lấy lịch chiếu theo rạp (Public) */
    suspend fun getShowtimesByCinema(cinemaId: Long, date: String): Result<List<CinemaShowtimeItem>>
}
