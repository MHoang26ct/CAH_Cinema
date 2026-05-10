package com.example.cah_cinema.presentation.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    // Movie Info
    val movieTitle: String = "HẸN EM NGÀY NHẬT THỰC",
    val movieAge: String = "T16",
    val posterUrl: String = ImageUrls.HEN_EM_NGAY_NHAT_THUC_BANNER,
    val tags: List<String> = listOf("Drama", "2D", "T16"),
    val ageNote: String = "Không dành cho khán giả dưới 16 tuổi",
    val duration: String = "118 phút",
    val selectedSeats: List<String> = emptyList(),
    
    // Cinema Info (Align with SeatSelectionViewModel defaults)
    val cinemaName: String = "Cinestar Quốc Thanh (TP.HCM)",
    val room: String = "03",
    val showtime: String = "18:20",
    val date: String = "09/04/2026",
    val subRoom: String = "01",
    
    // Timer (Increased to 10 minutes)
    val timeLeftSeconds: Int = 600, 
    val isTimeout: Boolean = false,
    
    // Pricing
    val ticketPricePerSeat: Double = 45000.0,
    val ticketTotal: Double = 0.0,
    val ticketQuantity: Int = 0,
    
    // Concession Info
    val concessionName: String = "Pepsi lớn",
    val concessionQuantity: Int = 1,
    val concessionTotal: Double = 0.0,
    
    val discount: Double = 0.0,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.MOMO,
    val isPaymentSuccessful: Boolean = false,
) {
    // Calculated totals
    val totalAmount: Double get() = ticketTotal + concessionTotal
    val finalAmount: Double get() = totalAmount - discount
    
    val timeLeftFormatted: String get() {
        val minutes = timeLeftSeconds / 60
        val seconds = timeLeftSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}

enum class PaymentMethod(val displayName: String) {
    MOMO("Momo"),
    ZALOPAY("Zalopay"),
    SHOPEEPAY("ShopeePay"),
    BANKING("Internet Banking")
}

/**
 * PaymentViewModel handles the UI logic for the Payment Screen.
 */
class PaymentViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    // BACKEND NOTE: Data passed from ConcessionScreen
    private val seatsArg: String = savedStateHandle["seats"] ?: ""
    private val totalAmountArg: Float = savedStateHandle["totalAmount"] ?: 0f
    private val dateArg: String = savedStateHandle["date"] ?: "06/04"
    private val timeArg: String = savedStateHandle["time"] ?: "18:20"

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        // Support both " : " and ", " as separators just in case
        val seatList = if (seatsArg.isNotEmpty()) {
            if (seatsArg.contains(" : ")) seatsArg.split(" : ") else seatsArg.split(", ")
        } else {
            emptyList()
        }
        
        val ticketPrice = 45000.0
        val ticketTotal = seatList.size * ticketPrice
        val concessionTotal = (totalAmountArg.toDouble() - ticketTotal).coerceAtLeast(0.0)

        val displayDate = dateArg.replace("-", "/")

        _uiState.update { it.copy(
            selectedSeats = seatList,
            ticketTotal = ticketTotal,
            ticketQuantity = seatList.size,
            concessionTotal = concessionTotal,
            concessionQuantity = if (concessionTotal > 0) 1 else 0,
            date = if (displayDate.length == 5) "$displayDate/2026" else displayDate,
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
            // Timer hit 0
            _uiState.update { it.copy(isTimeout = true) }
        }
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.update { it.copy(selectedPaymentMethod = method) }
    }

    fun onPaymentClick() {
        // Handle payment logic - Backend integration required
        // Simulating a payment check
        viewModelScope.launch {
            // Check if payment is already made via QR transfer
            // In a real app, this would be a server call
            _uiState.update { it.copy(isPaymentSuccessful = true) }
        }
    }
}
