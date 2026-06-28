package com.example.cah_cinema.data.model

/** Item suất chiếu hiển thị trên màn hình chọn suất bán vé */
data class StaffShowtimeItem(
    val showtimeId: Long,
    val movieTitle: String,
    val moviePosterUrl: String,
    val cinemaName: String,
    val roomName: String,
    val startTime: String,
    val endTime: String,
    val basePrice: Double,
    val availableSeats: Int = 0
)
