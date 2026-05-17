package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("user") val user: UserInfo,
    @SerializedName("recentInvoices") val recentInvoices: List<Invoice>
)

data class UserInfo(
    @SerializedName("userId") val userId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("authProvider") val authProvider: String,
    @SerializedName("role") val role: String,
    @SerializedName("totalPaid") val totalPaid: Double,
    @SerializedName("totalPoint") val totalPoint: Int,
    @SerializedName("rankLevel") val rankLevel: String
)

data class Invoice(
    @SerializedName("bookingId") val bookingId: Long,
    @SerializedName("bookingStatus") val bookingStatus: String? = null,
    @SerializedName("paymentMethod") val paymentMethod: String? = null,
    @SerializedName("discountAmount") val discountAmount: Double? = null,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("bookingCreatedAt") val bookingCreatedAt: String? = null,
    @SerializedName("voucherCode") val voucherCode: String? = null,
    @SerializedName("showtimeId") val showtimeId: Long? = null,
    @SerializedName("movieFormat") val movieFormat: String? = null,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String? = null,
    @SerializedName("movieId") val movieId: Long? = null,
    @SerializedName("movieTitle") val movieTitle: String,
    @SerializedName("moviePosterUrl") val moviePosterUrl: String,
    @SerializedName("cinemaName") val cinemaName: String,
    @SerializedName("roomName") val roomName: String? = null,
    @SerializedName("seats") val seats: List<InvoiceSeat>? = null,
    @SerializedName("foods") val foods: List<InvoiceFood>? = null,
    @SerializedName("foodTotalPrice") val foodTotalPrice: Double? = null
)

data class InvoiceSeat(
    @SerializedName("seatId") val seatId: Long,
    @SerializedName("seatRow") val seatRow: Double,
    @SerializedName("seatCol") val seatCol: Double,
    @SerializedName("seatType") val seatType: String,
    @SerializedName("ticketPrice") val ticketPrice: Double
)

data class InvoiceFood(
    @SerializedName("foodId") val foodId: Long,
    @SerializedName("foodName") val foodName: String,
    @SerializedName("foodImageUrl") val foodImageUrl: String,
    @SerializedName("foodCategory") val foodCategory: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: Double
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null
)
