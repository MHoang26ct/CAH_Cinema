package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class SeatTypeDTO(
    @SerializedName("seatTypeId") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("priceMultiplier") val multiplier: Double
)

data class SeatItem(
    @SerializedName("seatId") val id: Long,
    @SerializedName("row") val row: Double,
    @SerializedName("col") val col: Double,
    @SerializedName("rowLabel") val rowLabel: String,
    @SerializedName("colLabel") val colLabel: String,
    @SerializedName("seatType") val seatType: SeatTypeDTO,
    @SerializedName("status") val status: String,
    @SerializedName("isLocked") val isLocked: Boolean,
    @SerializedName("isSold") val isSold: Boolean,
    @SerializedName("occupancyStatus") val occupancyStatus: String
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
    @SerializedName("status") val status: String,
    @SerializedName("expiresAt") val expiresAt: String,
    @SerializedName("seatSubtotal") val seatSubtotal: Double,
    @SerializedName("foodSubtotal") val foodSubtotal: Double,
    @SerializedName("discountAmount") val discountAmount: Double,
    @SerializedName("totalAmount") val totalAmount: Double
)

data class FoodItem(
    @SerializedName("foodId") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String,
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("available") val available: Boolean = true
)

data class ConfirmPaymentRequest(
    @SerializedName("paymentRef") val paymentRef: String,
    @SerializedName("gateway") val gateway: String
)

data class ConfirmPaymentResponse(
    @SerializedName("bookingId") val bookingId: Long,
    @SerializedName("status") val status: String,
    @SerializedName("paymentRef") val paymentRef: String,
    @SerializedName("gateway") val gateway: String,
    @SerializedName("ticketStatus") val ticketStatus: String
)
