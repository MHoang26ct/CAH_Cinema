package com.example.cah_cinema.presentation.user.booking

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.util.DateTimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class PaymentUiState(
    // Thông tin phim
    val movieTitle: String = "",
    val movieAge: String = "T13",
    val posterUrl: String = "",
    val tags: List<String> = emptyList(),
    val ageNote: String = "",
    val duration: String = "",
    val selectedSeats: List<String> = emptyList(),

    // Thông tin rạp
    val cinemaName: String = "",
    val room: String = "",
    val showtime: String = "",
    val date: String = "",
    val subRoom: String = "",

    val timeLeftSeconds: Int = 600,
    val isTimeout: Boolean = false,

    // Giá cả
    val ticketPricePerSeat: Double = 0.0,
    val ticketTotal: Double = 0.0,
    val ticketQuantity: Int = 0,

    // Thông tin bắp nước (hiển thị tóm tắt)
    val concessionSummary: List<ConcessionSummaryItem> = emptyList(),
    val concessionTotal: Double = 0.0,

    // Voucher
    val selectedVoucherName: String? = null,
    val selectedVoucherId: Long? = null,
    val voucherDiscount: Double = 0.0,
    val discount: Double = 0.0,

    val totalAmount: Double = 0.0,
    val finalAmount: Double = 0.0,

    val selectedPaymentMethod: PaymentMethod = PaymentMethod.VNPAY,

    // Booking result
    val bookingId: Long? = null,
    val isPaymentSuccessful: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    
    // E-Payment info
    val paymentUrl: String? = null,
    val qrCodeUrl: String? = null,
    val isWaitingForPayment: Boolean = false
) {
    val timeLeftFormatted: String
        get() {
            val minutes = timeLeftSeconds / 60
            val seconds = timeLeftSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}

data class ConcessionSummaryItem(
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
    val imageUrl: String,
    val foodId: Long
)

enum class PaymentMethod(val displayName: String) {
    VNPAY("VNPAY")
}

class PaymentViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val showtimeId: String = savedStateHandle["showtimeId"] ?: ""
    private val seatIds: String = savedStateHandle["seatIds"] ?: ""
    private val seatsDisplay: String = savedStateHandle["seatsDisplay"] ?: ""
    private val totalAmountArg: Float = savedStateHandle["totalAmount"] ?: 0f
    private val dateArg: String = savedStateHandle["date"] ?: ""
    private val timeArg: String = savedStateHandle["time"] ?: ""

    // Food items được set từ ConcessionScreen trước khi navigate sang đây
    private var pendingFoodItems: List<ConcessionSummaryItem> = emptyList()
    private var pollingJob: Job? = null

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        val seatList = if (seatsDisplay.isNotEmpty()) {
            if (seatsDisplay.contains(" : ")) seatsDisplay.split(" : ")
            else seatsDisplay.split(", ")
        } else emptyList()

        _uiState.update { state ->
            state.copy(
                selectedSeats = seatList,
                ticketQuantity = seatList.size,
                totalAmount = totalAmountArg.toDouble(),
                finalAmount = totalAmountArg.toDouble(),
                date = DateTimeUtils.navDateToDisplay(dateArg),
                showtime = timeArg
            )
        }

        loadMissingData()
        startTimer()
    }

    private fun loadMissingData() {
        val movieIdStr: String? = savedStateHandle["movieId"]
        val mId = movieIdStr?.toLongOrNull() ?: return
        val stId = showtimeId.toLongOrNull() ?: return

        viewModelScope.launch {
            try {
                val context = loadBookingShowtimeContext(mId, stId, dateArg)
                _uiState.update { state ->
                    state.copy(
                        movieTitle = context.movie?.title ?: state.movieTitle,
                        posterUrl = context.movie?.posterUrl ?: state.posterUrl,
                        movieAge = context.movie?.age ?: state.movieAge,
                        duration = context.movie?.duration ?: state.duration,
                        cinemaName = context.cinemaName.ifBlank { state.cinemaName },
                        room = context.roomName.ifBlank { state.room }
                    )
                }
            } catch (_: Exception) { }
        }
    }

    fun setFoodItems(items: List<ConcessionSummaryItem>) {
        pendingFoodItems = items
        val concessionTotal = items.sumOf { it.unitPrice * it.quantity }
        _uiState.update { state ->
            // Tính lại totalAmount = tiền ghế (từ nav arg) + tiền bắp nước
            val newTotal = totalAmountArg.toDouble() + concessionTotal
            // Cập nhật finalAmount theo voucher đã áp dụng (nếu có)
            val newFinal = (newTotal - state.discount).coerceAtLeast(0.0)
            state.copy(
                concessionSummary = items,
                concessionTotal = concessionTotal,
                totalAmount = newTotal,
                finalAmount = newFinal
            )
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.timeLeftSeconds > 0 && !_uiState.value.isPaymentSuccessful) {
                delay(1000)
                _uiState.update { state -> state.copy(timeLeftSeconds = state.timeLeftSeconds - 1) }
            }
            if (_uiState.value.timeLeftSeconds <= 0 && !_uiState.value.isPaymentSuccessful) {
                _uiState.update { state -> state.copy(isTimeout = true) }
                stopPolling()
            }
        }
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.update { state -> state.copy(selectedPaymentMethod = method) }
    }

    fun applyVoucher(voucherName: String, voucherId: Long, discountAmount: Double) {
        _uiState.update { state ->
            val newFinalAmount = (state.totalAmount - discountAmount).coerceAtLeast(0.0)
            state.copy(
                selectedVoucherName = voucherName,
                selectedVoucherId = voucherId,
                voucherDiscount = discountAmount,
                discount = discountAmount,
                finalAmount = newFinalAmount
            )
        }
    }

    fun onPaymentClick() {
        val stId = showtimeId.toLongOrNull() ?: run {
            _uiState.update { it.copy(errorMessage = "Thông tin suất chiếu không hợp lệ") }
            return
        }
        val sIds = seatIds.split(",").mapNotNull { it.trim().toLongOrNull() }
        if (sIds.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Chưa chọn ghế") }
            return
        }

        _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Bước 0: Unlock ghế trước rồi re-lock lại để gia hạn TTL
                // Lý do: lockSeat dùng Redis SET NX (setIfAbsent) — chỉ thành công nếu key CHƯA tồn tại.
                // Nếu user đang giữ ghế (key đã tồn tại dù do chính họ), preLockSeats sẽ thất bại.
                // Giải pháp: unlock hết trước, sau đó lock lại để gia hạn TTL.
                sIds.forEach { seatId ->
                    try { RetrofitClient.apiService.unlockSeat(seatId, stId) } catch (_: Exception) { }
                }

                val reLockResponse = RetrofitClient.apiService.preLockSeats(
                    PreLockRequest(showtimeId = stId, seatIds = sIds)
                )
                if (!reLockResponse.isSuccessful) {
                    val errMsg = when (reLockResponse.code()) {
                        400 -> "Ghế đã bị người khác đặt mất trong khi chờ. Vui lòng chọn ghế lại."
                        401 -> "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại."
                        else -> "Không thể giữ ghế (lỗi ${reLockResponse.code()}). Vui lòng thử lại."
                    }
                    _uiState.update { state -> state.copy(isLoading = false, errorMessage = errMsg) }
                    return@launch
                }

                // Bước 1: Tạo booking
                val foodItems = pendingFoodItems
                    .filter { it.quantity > 0 }
                    .map { FoodOrderItemRequest(foodId = it.foodId, quantity = it.quantity) }
                    .takeIf { it.isNotEmpty() }

                val request = CreateBookingRequest(
                    showtimeId = stId,
                    seatIds = sIds,
                    paymentMethod = _uiState.value.selectedPaymentMethod.displayName,
                    voucherId = _uiState.value.selectedVoucherId,
                    foodItems = foodItems
                )

                var bookingResponse = RetrofitClient.apiService.createBooking(request)

                // Workaround: nếu backend 500 khi có foodItems (bug backend với @CreatedDate),
                // thử lại không kèm food để user vẫn đặt được vé.
                // LƯU Ý: khi backend 500, catch block của BookingService gọi releaseSeatLocksByOwner
                // → Redis key bị xóa → cần re-lock lại trước khi retry.
                if (bookingResponse.code() == 500 && !foodItems.isNullOrEmpty()) {
                    Log.w("PaymentVM", "createBooking 500 with foodItems, re-locking and retrying without food...")

                    // Re-lock ghế vì backend đã release lock trong catch block
                    sIds.forEach { seatId ->
                        try { RetrofitClient.apiService.unlockSeat(seatId, stId) } catch (_: Exception) { }
                    }
                    val reLockRetry = RetrofitClient.apiService.preLockSeats(
                        PreLockRequest(showtimeId = stId, seatIds = sIds)
                    )
                    if (!reLockRetry.isSuccessful) {
                        _uiState.update { state -> state.copy(isLoading = false, errorMessage = "Ghế không còn khả dụng. Vui lòng chọn lại.") }
                        return@launch
                    }

                    val requestNoFood = request.copy(foodItems = null)
                    bookingResponse = RetrofitClient.apiService.createBooking(requestNoFood)
                    if (bookingResponse.isSuccessful && bookingResponse.body()?.code == 200) {
                        // Cập nhật lại UI: bỏ phần bắp nước khỏi tổng
                        val concessionTotal = _uiState.value.concessionTotal
                        _uiState.update { state ->
                            state.copy(
                                concessionSummary = emptyList(),
                                concessionTotal = 0.0,
                                totalAmount = (state.totalAmount - concessionTotal).coerceAtLeast(0.0),
                                finalAmount = (state.finalAmount - concessionTotal).coerceAtLeast(0.0)
                            )
                        }
                    }
                }

                if (!bookingResponse.isSuccessful || bookingResponse.body()?.code != 200) {
                    val httpCode = bookingResponse.code()
                    val bodyMsg = bookingResponse.body()?.message
                    val errMsg = when {
                        httpCode == 500 -> "Hệ thống đang gặp sự cố, vui lòng thử lại sau."
                        bodyMsg != null -> bodyMsg
                        else -> "Đặt vé thất bại (lỗi $httpCode)"
                    }
                    // Chỉ unlock ghế khi lỗi nghiệp vụ (4xx), không unlock khi lỗi server (5xx)
                    if (httpCode in 400..499) {
                        unlockSeats(stId, sIds)
                    }
                    _uiState.update { state -> state.copy(isLoading = false, errorMessage = errMsg) }
                    return@launch
                }

                val bookingId = bookingResponse.body()?.data?.id ?: run {
                    unlockSeats(stId, sIds)
                    _uiState.update { state -> state.copy(isLoading = false, errorMessage = "Không nhận được mã booking") }
                    return@launch
                }
                _uiState.update { state -> state.copy(bookingId = bookingId) }

                // Bước 2: Xử lý theo phương thức thanh toán
                when (_uiState.value.selectedPaymentMethod) {
                    PaymentMethod.VNPAY -> {
                        val vnpayResp = RetrofitClient.apiService.createVNPayPayment(bookingId, VNPayPaymentRequest(requestId = UUID.randomUUID().toString()))
                        if (vnpayResp.isSuccessful && vnpayResp.body()?.code == 200) {
                            val data = vnpayResp.body()?.data
                            _uiState.update { state -> state.copy(
                                paymentUrl = data?.payUrl,
                                isWaitingForPayment = true,
                                isLoading = false
                            ) }
                            startStatusPolling(bookingId)
                        } else {
                            _uiState.update { state -> state.copy(isLoading = false, errorMessage = "Lỗi kết nối cổng VNPay") }
                        }
                    }
                }

            } catch (e: Exception) {
                // Exception không mong muốn → thử unlock ghế
                unlockSeats(stId, sIds)
                _uiState.update { state -> state.copy(isLoading = false, errorMessage = e.message ?: "Lỗi kết nối") }
            }
        }
    }

    /** Unlock tất cả ghế đã chọn khi booking/payment thất bại */
    private fun unlockSeats(showtimeId: Long, seatIds: List<Long>) {
        viewModelScope.launch {
            seatIds.forEach { seatId ->
                try {
                    RetrofitClient.apiService.unlockSeat(seatId, showtimeId)
                } catch (_: Exception) { }
            }
        }
    }

    private fun startStatusPolling(bookingId: Long) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                delay(3000) // Poll mỗi 3 giây
                try {
                    val resp = RetrofitClient.apiService.getBookingStatus(bookingId)
                    if (resp.isSuccessful) {
                        val status = resp.body()?.data?.status
                        Log.d("PaymentPolling", "Booking $bookingId status: $status")
                        if (status == "PAID") {
                            _uiState.update { state -> state.copy(isPaymentSuccessful = true, isWaitingForPayment = false) }
                            break
                        } else if (status == "FAILED" || status == "CANCELLED") {
                            _uiState.update { state -> state.copy(isWaitingForPayment = false, errorMessage = "Giao dịch thất bại hoặc đã bị hủy") }
                            break
                        }
                    }
                } catch (e: Exception) {
                    Log.e("PaymentPolling", "Polling error", e)
                }
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopPolling()
    }
}
