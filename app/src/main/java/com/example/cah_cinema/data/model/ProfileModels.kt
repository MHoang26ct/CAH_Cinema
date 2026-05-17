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
    @SerializedName("movieTitle") val movieTitle: String,
    @SerializedName("moviePosterUrl") val moviePosterUrl: String,
    @SerializedName("cinemaName") val cinemaName: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("totalPrice") val totalPrice: Double
)

data class UpdateProfileRequest(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String? = null
)
