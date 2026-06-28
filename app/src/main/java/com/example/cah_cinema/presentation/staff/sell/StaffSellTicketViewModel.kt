package com.example.cah_cinema.presentation.staff.sell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.data.repository.StaffRepositoryImpl
import com.example.cah_cinema.domain.repository.StaffRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class StaffSellState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class StaffSeatState(
    val seats: List<SeatItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class StaffPaymentState {
    object Idle : StaffPaymentState()
    object Loading : StaffPaymentState()
    data class AwaitingPayment(val bookingId: Long, val totalAmount: Double) : StaffPaymentState()
    object Success : StaffPaymentState()
    data class Error(val message: String) : StaffPaymentState()
}

class StaffSellTicketViewModel(
    private val repository: StaffRepository = StaffRepositoryImpl()
) : ViewModel() {

    // ── Showtimes state ───────────────────────────────────────────────────────
    private val _showtimes = MutableStateFlow<List<CinemaShowtimeItem>>(emptyList())
    val showtimes: StateFlow<List<CinemaShowtimeItem>> = _showtimes.asStateFlow()

    private val _showtimesLoading = MutableStateFlow(false)
    val showtimesLoading: StateFlow<Boolean> = _showtimesLoading.asStateFlow()

    private val _showtimesError = MutableStateFlow<String?>(null)
    val showtimesError: StateFlow<String?> = _showtimesError.asStateFlow()

    // ── Cinemas state ─────────────────────────────────────────────────────────
    private val _cinemas = MutableStateFlow<List<CinemaItem>>(emptyList())
    val cinemas: StateFlow<List<CinemaItem>> = _cinemas.asStateFlow()

    private val _selectedCinemaId = MutableStateFlow<Long?>(null)
    val selectedCinemaId: StateFlow<Long?> = _selectedCinemaId.asStateFlow()

    private val _selectedDate = MutableStateFlow(
        java.time.LocalDate.now().toString() // yyyy-MM-dd
    )
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // ── Seat state ────────────────────────────────────────────────────────────
    private val _seatState = MutableStateFlow(StaffSeatState())
    val seatState: StateFlow<StaffSeatState> = _seatState.asStateFlow()

    val selectedSeats = MutableStateFlow<Set<Long>>(emptySet())

    // ── Payment state ─────────────────────────────────────────────────────────
    private val _paymentState = MutableStateFlow<StaffPaymentState>(StaffPaymentState.Idle)
    val paymentState: StateFlow<StaffPaymentState> = _paymentState.asStateFlow()

    init {
        loadCinemas()
    }

    fun loadCinemas() {
        viewModelScope.launch {
            repository.getCinemas()
                .onSuccess { list ->
                    _cinemas.value = list
                    // Tự chọn rạp đầu tiên
                    list.firstOrNull()?.let {
                        selectCinema(it.id)
                    }
                }
        }
    }

    fun selectCinema(cinemaId: Long) {
        _selectedCinemaId.value = cinemaId
        loadShowtimes(cinemaId, _selectedDate.value)
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
        _selectedCinemaId.value?.let { loadShowtimes(it, date) }
    }

    fun loadShowtimes(cinemaId: Long, date: String) {
        viewModelScope.launch {
            _showtimesLoading.value = true
            _showtimesError.value = null
            repository.getShowtimesByCinema(cinemaId, date)
                .onSuccess { list ->
                    _showtimes.value = list
                }
                .onFailure { e ->
                    _showtimesError.value = e.message ?: "Lỗi kết nối"
                }
            _showtimesLoading.value = false
        }
    }

    // ── Seat selection ────────────────────────────────────────────────────────

    fun loadSeats(showtimeId: Long) {
        viewModelScope.launch {
            _seatState.value = StaffSeatState(isLoading = true)
            selectedSeats.value = emptySet()
            repository.getSeatsByShowtime(showtimeId)
                .onSuccess { seats ->
                    _seatState.value = StaffSeatState(seats = seats)
                }
                .onFailure { e ->
                    _seatState.value = StaffSeatState(errorMessage = e.message ?: "Không thể tải ghế")
                }
        }
    }

    fun toggleSeat(seatId: Long, isAvailable: Boolean) {
        if (!isAvailable) return
        val current = selectedSeats.value.toMutableSet()
        if (current.contains(seatId)) current.remove(seatId) else current.add(seatId)
        selectedSeats.value = current
    }

    fun calculateTotal(seats: List<SeatItem>, basePrice: Double): Double {
        val selected = selectedSeats.value
        return seats
            .filter { it.id in selected }
            .sumOf { (it.seatType?.priceMultiplier ?: 1.0) * basePrice }
    }

    // ── Payment ───────────────────────────────────────────────────────────────

    fun createBookingAndAwaitPayment(showtimeId: Long) {
        if (selectedSeats.value.isEmpty()) return
        viewModelScope.launch {
            _paymentState.value = StaffPaymentState.Loading
            repository.createCashBooking(
                showtimeId = showtimeId,
                seatIds = selectedSeats.value.toList()
            ).onSuccess { booking ->
                _paymentState.value = StaffPaymentState.AwaitingPayment(
                    bookingId = booking.id,
                    totalAmount = booking.totalAmount
                )
            }.onFailure { e ->
                _paymentState.value = StaffPaymentState.Error(e.message ?: "Tạo đặt vé thất bại")
            }
        }
    }

    fun confirmCashPayment(bookingId: Long) {
        viewModelScope.launch {
            _paymentState.value = StaffPaymentState.Loading
            repository.confirmCashPayment(bookingId)
                .onSuccess {
                    _paymentState.value = StaffPaymentState.Success
                }
                .onFailure { e ->
                    _paymentState.value = StaffPaymentState.Error(e.message ?: "Xác nhận thanh toán thất bại")
                }
        }
    }

    fun resetPayment() {
        _paymentState.value = StaffPaymentState.Idle
        selectedSeats.value = emptySet()
    }
}
