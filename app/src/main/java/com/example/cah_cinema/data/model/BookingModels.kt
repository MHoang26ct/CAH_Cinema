package com.example.cah_cinema.data.model

import com.google.gson.annotations.SerializedName

data class SeatItem(
    @SerializedName("seatId") val id: Long,
    @SerializedName("row") val row: Double,
    @SerializedName("col") val col: Double,
    @SerializedName("rowLabel") val rowLabel: String?,   // null = aisle ngang
    @SerializedName("colLabel") val colLabel: String?,   // null = aisle dọc
    @SerializedName("seatType") val seatType: SeatTypeItem?,  // nullable — AISLE có thể null
    @SerializedName("status") val status: String = "ACTIVE",
    @SerializedName("isLocked") val isLocked: Boolean = false,
    @SerializedName("isSold") val isSold: Boolean = false,
    @SerializedName("occupancyStatus") val occupancyStatus: String = "AVAILABLE" // AVAILABLE, LOCKED, SOLD
)

data class SeatTypeItem(
    @SerializedName("seatTypeId") val seatTypeId: Long = 0,
    @SerializedName("typeName") val typeName: String = "REGULAR",    // REGULAR, VIP, COUPLE, AISLE
    @SerializedName("priceMultiplier") val priceMultiplier: Double = 1.0
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
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("status") val status: String,
    @SerializedName("expiresAt") val expiresAt: String? = null,
    @SerializedName("seatSubtotal") val seatSubtotal: Double? = null,
    @SerializedName("foodSubtotal") val foodSubtotal: Double? = null,
    @SerializedName("discountAmount") val discountAmount: Double? = null
)

data class FoodItem(
    @SerializedName("foodId") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("category") val category: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("available") val available: Boolean = true
)

data class ConfirmPaymentRequest(
    @SerializedName("paymentRef") val paymentRef: String,
    @SerializedName("gateway") val gateway: String
)

data class MoMoPaymentRequest(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("requestType") val requestType: String = "captureWallet"
)

data class MoMoPaymentResponse(
    @SerializedName("payUrl") val payUrl: String,
    @SerializedName("deeplink") val deeplink: String?,
    @SerializedName("qrCodeUrl") val qrCodeUrl: String?
)

data class VNPayPaymentRequest(
    @SerializedName("requestId") val requestId: String
)

data class VNPayPaymentResponse(
    @SerializedName("payUrl") val payUrl: String
)

data class CheckInRequest(
    @SerializedName("qrToken") val qrToken: String
)

data class CheckInResponse(
    @SerializedName("ticketId") val ticketId: Long,
    @SerializedName("bookingId") val bookingId: Long,
    @SerializedName("movieTitle") val movieTitle: String,
    @SerializedName("cinemaName") val cinemaName: String,
    @SerializedName("roomName") val roomName: String,
    @SerializedName("showtimeStart") val startTime: String,
    @SerializedName("seatName") val seatName: String
)

/** Request tạo booking tại quầy (staff bán vé trực tiếp, thanh toán tiền mặt) */
data class StaffCreateBookingRequest(
    @SerializedName("showtimeId") val showtimeId: Long,
    @SerializedName("seatIds") val seatIds: List<Long>,
    @SerializedName("paymentMethod") val paymentMethod: String = "CASH",
    @SerializedName("voucherId") val voucherId: Long? = null,
    @SerializedName("foodItems") val foodItems: List<FoodOrderItemRequest>? = null
)

/** Response sau khi staff xác nhận thanh toán cash */
data class StaffConfirmPaymentRequest(
    @SerializedName("paymentRef") val paymentRef: String = "CASH",
    @SerializedName("gateway") val gateway: String = "CASH"
)
