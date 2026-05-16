package com.example.cah_cinema.presentation.user.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.CreateBookingRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for Payment Screen
 */
data class PaymentUiState(
    // Thông tin phim
    val movieTitle: String = "HẸN EM NGÀY NHẬT THỰC",
    val movieAge: String = "T16",
    val posterUrl: String = ImageUrls.HEN_EM_NGAY_NHAT_THUC_BANNER,
    val tags: List<String> = listOf("Drama", "2D", "T16"),
    val ageNote: String = "Không dành cho khán giả dưới 16 tuổi",
    val duration: String = "118 phút",
    val selectedSeats: List<String> = emptyList(),
    
    // Thông tin rạp
    val cinemaName: String = "Cinestar Quốc Thanh (TP.HCM)",
    val room: String = "03",
    val showtime: String = "18:20",
    val date: String = "09/04/2026",
    val subRoom: String = "01",
    
    val timeLeftSeconds: Int = 600, 
    val isTimeout: Boolean = false,
    
    // Giá cả
    val ticketPricePerSeat: Double = 45000.0,
    val ticketTotal: Double = 0.0,
    val ticketQuantity: Int = 0,
    
    // Thông tin bắp nước
    val concessionName: String = "Pepsi lớn",
    val concessionQuantity: Int = 1,
    val concessionTotal: Double = 0.0,
    
    val discount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val finalAmount: Double = 0.0,
    val selectedVoucherName: String? = null,
    val voucherDiscount: Double = 0.0,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.MOMO,
    val isPaymentSuccessful: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val timeLeftFormatted: String get() {
        val minutes = timeLeftSeconds / 60
        val seconds = timeLeftSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

enum class PaymentMethod(val displayName: String) {
    MOMO("MOMO"),
    ZALOPAY("VNPAY"),
    SHOPEEPAY("CASH"),
    BANKING("Internet Banking")
}

/**
 * PaymentViewModel handles the UI logic for the Payment Screen.
 */
class PaymentViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val showtimeId: String = savedStateHandle["showtimeId"] ?: ""
    private val seatIds: String = savedStateHandle["seatIds"] ?: ""
    private val seatsDisplay: String = savedStateHandle["seatsDisplay"] ?: ""
    private val totalAmountArg: Float = savedStateHandle["totalAmount"] ?: 0f
    private val dateArg: String = savedStateHandle["date"] ?: ""
    private val timeArg: String = savedStateHandle["time"] ?: ""

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        val seatList = if (seatsDisplay.isNotEmpty()) {
            if (seatsDisplay.contains(" : ")) seatsDisplay.split(" : ") else seatsDisplay.split(", ")
        } else {
            emptyList()
        }

        _uiState.update { it.copy(
            selectedSeats = seatList,
            ticketTotal = seatList.size * 45000.0, 
            ticketQuantity = seatList.size,
            concessionTotal = (totalAmountArg.toDouble() - (seatList.size * 45000.0)).coerceAtLeast(0.0),
            totalAmount = totalAmountArg.toDouble(),
            finalAmount = totalAmountArg.toDouble(),
            date = dateArg,
            showtime = timeArg
        ) }
        
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.timeLeftSeconds > 0) {
                delay(1000)
                _uiState.update { it.copy(timeLeftSeconds = it.timeLeftSeconds - 1) }
            }
            _uiState.update { it.copy(isTimeout = true) }
        }
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    fun applyVoucher(voucherName: String, discountAmount: Double) {
        _uiState.update { 
            val newFinalAmount = (it.totalAmount - discountAmount).coerceAtLeast(0.0)
            it.copy(
                selectedVoucherName = voucherName,
                voucherDiscount = discountAmount,
                discount = discountAmount,
                finalAmount = newFinalAmount
            )
        }
    }

    fun onPaymentClick() {
        val stId = showtimeId.toLongOrNull() ?: return
        val sIds = seatIds.split(",").mapNotNull { it.toLongOrNull() }
        
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                val request = CreateBookingRequest(
                    showtimeId = stId,
                    seatIds = sIds,
                    paymentMethod = _uiState.value.selectedPaymentMethod.displayName
                    // foodItems and voucherId can be added if state expanded
                )
                val response = RetrofitClient.apiService.createBooking(request)
                if (response.isSuccessful && response.body()?.code == 200) {
                    _uiState.update { it.copy(isPaymentSuccessful = true, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = response.body()?.message ?: "Đặt vé thất bại") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
