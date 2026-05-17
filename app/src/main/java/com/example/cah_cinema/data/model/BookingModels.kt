package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class SeatItem(
    @SerializedName("seatId") val id: Long,
    @SerializedName("row") val row: Double,
    @SerializedName("col") val col: Double,
    @SerializedName("seatType") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("price") val price: Double
)

data class CreateBookingRequest(
    @SerializedName("showtimeId") val showtimeId: Long,
    @SerializedName("seatIds") val seatIds: List<Long>,
    @SerializedName("paymentMethod") val paymentMethod: String,
    @SerializedName("voucherId") val voucherId: Long? = null,
    @SerializedName("foodItems") val foodItems: List<FoodOrderItemRequest>? = null
)

data class FoodOrderItemRequest(
    @SerializedName("foodId") val foodId: Long,
    @SerializedName("quantity") val quantity: Int
)

data class BookingData(
    @SerializedName("bookingId") val id: Long,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("status") val status: String
)

data class FoodItem(
    @SerializedName("foodId") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String,
    @SerializedName("imageUrl") val imageUrl: String
)

data class ConfirmPaymentRequest(
    @SerializedName("paymentRef") val paymentRef: String,
    @SerializedName("gateway") val gateway: String
)
