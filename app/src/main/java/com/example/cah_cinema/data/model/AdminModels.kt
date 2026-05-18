package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

// Report Models
data class BusinessOverviewResponse(
    @SerializedName("totalRevenue") val totalRevenue: Double,
    @SerializedName("ticketRevenue") val ticketRevenue: Double,
    @SerializedName("foodRevenue") val foodRevenue: Double,
    @SerializedName("totalTicketsSold") val totalTicketsSold: Int,
    @SerializedName("totalBookingsPaid") val totalBookingsPaid: Int,
    @SerializedName("activeMovies") val activeMovies: Int
)

data class DailyRevenueResponse(
    @SerializedName("date") val date: String,
    @SerializedName("revenue") val revenue: Double,
    @SerializedName("ticketCount") val ticketCount: Int
)

data class MovieRevenueResponse(
    @SerializedName("movieId") val movieId: Long,
    @SerializedName("movieTitle") val movieTitle: String,
    @SerializedName("revenue") val revenue: Double,
    @SerializedName("ticketCount") val ticketCount: Int
)

data class CinemaRevenueResponse(
    @SerializedName("cinemaId") val cinemaId: Long,
    @SerializedName("cinemaName") val cinemaName: String,
    @SerializedName("revenue") val revenue: Double,
    @SerializedName("ticketCount") val ticketCount: Int
)

// Movie Admin Models
data class UpdateOrCreateMovieRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("releaseDate") val releaseDate: String, // yyyy-MM-dd
    @SerializedName("ageRating") val ageRating: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("trailerUrl") val trailerUrl: String,
    @SerializedName("directorName") val directorName: String,
    @SerializedName("actorList") val actorList: String,
    @SerializedName("genreIdList") val genreIdList: List<Long>
)

// Cinema Admin Models
data class CreateCinemaRequest(
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("hotline") val hotline: String,
    @SerializedName("imageUrl") val imageUrl: String? = null
)

data class CreateRoomRequest(
    @SerializedName("cinemaId") val cinemaId: Long,
    @SerializedName("roomName") val roomName: String
)

// Showtime Admin Models
data class CreateShowtimeRequest(
    @SerializedName("movieId") val movieId: Long,
    @SerializedName("roomId") val roomId: Long,
    @SerializedName("format") val format: String, // 2D, 3D, IMAX
    @SerializedName("startTime") val startTime: String, // ISO date-time
    @SerializedName("endTime") val endTime: String, // ISO date-time
    @SerializedName("basePrice") val basePrice: Double
)

data class UpdateShowtimeRequest(
    @SerializedName("showtimeId") val showtimeId: Long,
    @SerializedName("movieId") val movieId: Long,
    @SerializedName("roomId") val roomId: Long,
    @SerializedName("format") val format: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("basePrice") val basePrice: Double,
    @SerializedName("status") val status: String // AVAILABLE, SOLD_OUT, HIDDEN
)

// Voucher Admin Models
data class CreateVoucherRequest(
    @SerializedName("code") val code: String,
    @SerializedName("type") val type: String, // FIXED_AMOUNT, PERCENT
    @SerializedName("value") val value: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("startAt") val startAt: String,
    @SerializedName("expiredAt") val expiredAt: String,
    @SerializedName("minOrderValue") val minOrderValue: Double?,
    @SerializedName("maxDiscount") val maxDiscount: Double?
)

data class UpdateVoucherRequest(
    @SerializedName("voucherId") val voucherId: Long,
    @SerializedName("code") val code: String,
    @SerializedName("type") val type: String,
    @SerializedName("value") val value: Double,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("startAt") val startAt: String,
    @SerializedName("expiredAt") val expiredAt: String,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("isDeleted") val isDeleted: Boolean,
    @SerializedName("minOrderValue") val minOrderValue: Double?,
    @SerializedName("maxDiscount") val maxDiscount: Double?
)

// Seat Admin Models
data class CreateSeatRequest(
    @SerializedName("roomId") val roomId: Long,
    @SerializedName("row") val row: Double,
    @SerializedName("col") val col: Double,
    @SerializedName("seatTypeId") val seatTypeId: Long
)

// Config & Holiday Models
data class PriceConfig(
    @SerializedName("configId") val id: Long? = null,
    @SerializedName("multiplier") val multiplier: Double? = null,
    @SerializedName("dayType") val dayType: String? = null, // WEEKDAY, WEEKEND, HOLIDAY
    @SerializedName("timeSlot") val timeSlot: String? = null, // MORNING, AFTERNOON, EVENING
    @SerializedName("movieFormat") val movieFormat: String? = null // 2D, 3D, IMAX
)

data class Holiday(
    @SerializedName("holidayId") val id: Long? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("isRecurring") val isRecurring: Boolean = false
)

data class DeleteHolidayRequest(
    @SerializedName("holidayId") val holidayId: Long
)
