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
    @SerializedName("bookingStatus") val bookingStatus: String,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("discountAmount") val discountAmount: Double,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("bookingCreatedAt") val createdAt: String,
    @SerializedName("voucherCode") val voucherCode: String?,
    @SerializedName("showtimeId") val showtimeId: Long,
    @SerializedName("movieFormat") val movieFormat: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("movieId") val movieId: Long,
    @SerializedName("movieTitle") val movieTitle: String,
    @SerializedName("moviePosterUrl") val moviePosterUrl: String,
    @SerializedName("cinemaName") val cinemaName: String,
    @SerializedName("roomName") val roomName: String,
    @SerializedName("seats") val seats: List<InvoiceSeat>,
    @SerializedName("foods") val foods: List<InvoiceFood>,
    @SerializedName("foodTotalPrice") val foodTotalPrice: Double
)

data class InvoiceSeat(
    @SerializedName("seatId") val id: Long,
    @SerializedName("seatRow") val row: Double,
    @SerializedName("seatCol") val col: Double,
    @SerializedName("seatType") val type: String,
    @SerializedName("ticketPrice") val price: Double
)

data class InvoiceFood(
    @SerializedName("foodId") val id: Long,
    @SerializedName("foodName") val name: String,
    @SerializedName("foodImageUrl") val imageUrl: String,
    @SerializedName("foodCategory") val category: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val price: Double
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null
)
