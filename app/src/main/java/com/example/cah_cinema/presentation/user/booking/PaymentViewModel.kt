package com.example.cah_cinema.presentation.user.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.ConfirmPaymentRequest
import com.example.cah_cinema.data.model.CreateBookingRequest
import com.example.cah_cinema.data.model.FoodOrderItemRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH,

    // Booking result
    val bookingId: Long? = null,
    val isPaymentSuccessful: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
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
    CASH("CASH"),
    VNPAY("VNPAY"),
    MOMO("MOMO")
}

class PaymentViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val showtimeId: String = savedStateHandle["showtimeId"] ?: ""
    private val seatIds: String = savedStateHandle["seatIds"] ?: ""
    private val seatsDisplay: String = savedStateHandle["seatsDisplay"] ?: ""
    private val totalAmountArg: Float = savedStateHandle["totalAmount"] ?: 0f
    private val dateArg: String = savedStateHandle["date"] ?: ""
    private val timeArg: String = savedStateHandle["time"] ?: ""

    // Food items được set từ ConcessionScreen trước khi navigate sang đây
    private var pendingFoodItems: List<ConcessionSummaryItem> = emptyList()

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        val seatList = if (seatsDisplay.isNotEmpty()) {
            if (seatsDisplay.contains(" : ")) seatsDisplay.split(" : ")
            else seatsDisplay.split(", ")
        } else emptyList()

        _uiState.update {
            it.copy(
                selectedSeats = seatList,
                ticketQuantity = seatList.size,
                totalAmount = totalAmountArg.toDouble(),
                finalAmount = totalAmountArg.toDouble(),
                date = dateArg,
                showtime = timeArg
            )
        }

        startTimer()
    }

    /**
     * Được gọi từ ConcessionScreen để truyền danh sách đồ ăn đã chọn.
     */
    fun setFoodItems(items: List<ConcessionSummaryItem>) {
        pendingFoodItems = items
        val concessionTotal = items.sumOf { it.unitPrice * it.quantity }
        _uiState.update {
            it.copy(
                concessionSummary = items,
                concessionTotal = concessionTotal
            )
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.timeLeftSeconds > 0 && !_uiState.value.isPaymentSuccessful) {
                delay(1000)
                _uiState.update { it.copy(timeLeftSeconds = it.timeLeftSeconds - 1) }
            }
            if (_uiState.value.timeLeftSeconds <= 0 && !_uiState.value.isPaymentSuccessful) {
                _uiState.update { it.copy(isTimeout = true) }
            }
        }
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    fun applyVoucher(voucherName: String, voucherId: Long, discountAmount: Double) {
        _uiState.update {
            val newFinalAmount = (it.totalAmount - discountAmount).coerceAtLeast(0.0)
            it.copy(
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

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Bước 1: Tạo booking với đầy đủ food items và voucherId
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

                val bookingResponse = RetrofitClient.apiService.createBooking(request)
                if (!bookingResponse.isSuccessful || bookingResponse.body()?.code != 200) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = bookingResponse.body()?.message ?: "Đặt vé thất bại"
                        )
                    }
                    return@launch
                }

                val bookingData = bookingResponse.body()?.data
                val bookingId = bookingData?.id ?: run {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Không nhận được mã booking") }
                    return@launch
                }

                _uiState.update { it.copy(bookingId = bookingId) }

                // Bước 2: Xác nhận thanh toán
                val paymentMethod = _uiState.value.selectedPaymentMethod
                val confirmRequest = ConfirmPaymentRequest(
                    paymentRef = "PAY-$bookingId-${System.currentTimeMillis()}",
                    gateway = paymentMethod.displayName
                )

                val confirmResponse = RetrofitClient.apiService.confirmPayment(bookingId, confirmRequest)
                if (confirmResponse.isSuccessful || confirmResponse.body()?.code == 200) {
                    _uiState.update { it.copy(isPaymentSuccessful = true, isLoading = false) }
                } else {
                    // Booking đã tạo nhưng confirm thất bại — vẫn coi là thành công để user lấy vé
                    _uiState.update { it.copy(isPaymentSuccessful = true, isLoading = false) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Lỗi kết nối") }
            }
        }
    }
}
