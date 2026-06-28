package com.example.cah_cinema.presentation.user.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.Seat
import com.example.cah_cinema.domain.model.SeatStatus
import com.example.cah_cinema.domain.model.SeatType
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.data.model.SeatItem
import com.example.cah_cinema.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SeatSelectionState(
    val movie: Movie? = null,
    val selectedCinemaName: String = "",
    val selectedRoom: String = "",
    val selectedShowtime: String = "",
    val selectedDate: String = "",
    val seats: List<Seat> = emptyList(),
    val selectedSeats: List<Seat> = emptyList(),
    val regularTicketsCount: Int = 0,
    val coupleTicketsCount: Int = 0,
    val basePrice: Double = 0.0,
    val extraPrice: Double = 0.0,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)

class SeatSelectionViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(SeatSelectionState())
    val state: StateFlow<SeatSelectionState> = _state.asStateFlow()

    private val movieId: String? = savedStateHandle["movieId"]
    private val showtimeId: String? = savedStateHandle["showtimeId"]
    private val dateArg: String = savedStateHandle["date"] ?: ""
    private val timeArg: String = savedStateHandle["time"] ?: ""
    private val regularCount: Int = savedStateHandle.get<Int>("regularCount") ?: 0
    private val coupleCount: Int = savedStateHandle.get<Int>("coupleCount") ?: 0
    private val basePriceArg: Long = savedStateHandle.get<Long>("basePrice") ?: 0L

    init {
        _state.update {
            it.copy(
                regularTicketsCount = regularCount,
                coupleTicketsCount = coupleCount,
                basePrice = basePriceArg.toDouble(),
                selectedShowtime = timeArg,
                selectedDate = DateTimeUtils.navDateToDisplay(dateArg)
            )
        }
        loadBookingContext()
        loadSeats()
    }

    private fun loadBookingContext() {
        val mId = movieId?.toLongOrNull() ?: return
        val stId = showtimeId?.toLongOrNull() ?: return

        viewModelScope.launch {
            try {
                val context = loadBookingShowtimeContext(mId, stId, dateArg)
                _state.update { current ->
                    current.copy(
                        movie = context.movie ?: current.movie,
                        selectedCinemaName = context.cinemaName.ifBlank { current.selectedCinemaName },
                        selectedRoom = if (context.roomName.isNotEmpty()) {
                            "Phòng: ${context.roomName}"
                        } else {
                            current.selectedRoom
                        },
                        basePrice = if (context.basePrice > 0) context.basePrice else current.basePrice
                    )
                }
            } catch (_: Exception) { }
        }
    }

    private fun loadSeats() {
        val id = showtimeId?.toLongOrNull() ?: return

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getSeats(id)
                if (response.isSuccessful) {
                    val seatItems = response.body()?.data ?: emptyList()
                    if (seatItems.isEmpty()) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                seats = emptyList(),
                                errorMessage = "Phòng chưa có sơ đồ ghế. Admin cần thiết kế ghế trước khi đặt vé."
                            )
                        }
                        return@launch
                    }
                    val domainSeats = seatItems.map { it.toDomainSeatOrNull() }
                    _state.update { current ->
                        // Ghế nào đang trong selectedSeats (user đang giữ) mà backend trả LOCKED
                        // → phục hồi trạng thái SELECTED thay vì TAKEN_BY_OTHERS
                        val alreadySelectedIds = current.selectedSeats.map { it.id }.toSet()
                        val correctedSeats = domainSeats.map { seat ->
                            if (seat.status == SeatStatus.TAKEN_BY_OTHERS && seat.id in alreadySelectedIds) {
                                seat.copy(status = SeatStatus.SELECTED)
                            } else {
                                seat
                            }
                        }
                        current.copy(seats = correctedSeats, isLoading = false)
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Không thể tải sơ đồ ghế (${response.code()})"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Lỗi kết nối") }
            }
        }
    }

    private fun SeatItem.toDomainSeatOrNull(): Seat {
        // seatType có thể null nếu ghế là AISLE; fallback về REGULAR/AISLE dựa trên rowLabel/colLabel
        val seatTypeName = seatType?.typeName
            ?: if (rowLabel == null || colLabel == null) "AISLE" else "REGULAR"
        // PREPARE = ghế chưa kích hoạt, không cho đặt (hiển thị như bảo trì)
        val occupancy = if (status == "PREPARE") "MAINTENANCE" else occupancyStatus
        return Seat(
            id = id.toString(),
            row = row,
            col = col,
            rowLabel = rowLabel,
            colLabel = colLabel,
            type = when (seatTypeName) {
                "COUPLE" -> SeatType.COUPLE
                "VIP" -> SeatType.VIP
                "AISLE" -> SeatType.AISLE
                else -> SeatType.REGULAR  // "NORMAL", "REGULAR", ...
            },
            status = when (occupancy) {
                "SOLD" -> SeatStatus.BOOKED
                "LOCKED" -> SeatStatus.TAKEN_BY_OTHERS
                "MAINTENANCE" -> SeatStatus.MAINTENANCE
                else -> SeatStatus.AVAILABLE
            }
        )
    }

    fun onSeatClick(seat: Seat) {
        when (seat.status) {
            SeatStatus.BOOKED, SeatStatus.TAKEN_BY_OTHERS -> {
                _state.update { it.copy(errorMessage = "Ghế này không khả dụng") }
                return
            }
            SeatStatus.MAINTENANCE -> {
                _state.update { it.copy(errorMessage = "Ghế đang bảo trì") }
                return
            }
            else -> {}
        }

        val stId = showtimeId?.toLongOrNull() ?: return
        val seatIdLong = seat.id.toLongOrNull() ?: return

        val isCurrentlySelected = _state.value.selectedSeats.any { it.id == seat.id }

        if (!isCurrentlySelected) {
            // Kiểm tra số vé cho phép
            val totalTicketsAllowed = _state.value.regularTicketsCount + (_state.value.coupleTicketsCount * 2)
            if (_state.value.selectedSeats.size >= totalTicketsAllowed) return

            // Optimistic UI: hiển thị SELECTED ngay
            _state.update { currentState ->
                val newSelectedSeats = currentState.selectedSeats + seat
                val updatedSeats = currentState.seats.map {
                    if (it.id == seat.id) it.copy(status = SeatStatus.SELECTED) else it
                }
                currentState.copy(seats = updatedSeats, selectedSeats = newSelectedSeats, errorMessage = null)
            }

            // Gọi API lock — nếu thất bại thì rollback UI
            viewModelScope.launch {
                try {
                    val response = RetrofitClient.apiService.lockSeat(seatIdLong, stId)
                    if (!response.isSuccessful) {
                        // Lock thất bại (ghế đã bị người khác giữ hoặc lỗi khác) → rollback
                        _state.update { currentState ->
                            val newSelectedSeats = currentState.selectedSeats.filter { it.id != seat.id }
                            val updatedSeats = currentState.seats.map {
                                if (it.id == seat.id) it.copy(status = SeatStatus.TAKEN_BY_OTHERS) else it
                            }
                            currentState.copy(
                                seats = updatedSeats,
                                selectedSeats = newSelectedSeats,
                                errorMessage = if (response.code() == 400) "Ghế vừa bị người khác chọn" else "Không thể chọn ghế này"
                            )
                        }
                    }
                } catch (_: Exception) {
                    // Lỗi mạng → rollback về AVAILABLE để user thử lại
                    _state.update { currentState ->
                        val newSelectedSeats = currentState.selectedSeats.filter { it.id != seat.id }
                        val updatedSeats = currentState.seats.map {
                            if (it.id == seat.id) it.copy(status = SeatStatus.AVAILABLE) else it
                        }
                        currentState.copy(
                            seats = updatedSeats,
                            selectedSeats = newSelectedSeats,
                            errorMessage = "Lỗi kết nối khi chọn ghế. Vui lòng thử lại."
                        )
                    }
                }
            }
        } else {
            // Bỏ chọn ghế → optimistic UI rồi unlock
            _state.update { currentState ->
                val newSelectedSeats = currentState.selectedSeats.filter { it.id != seat.id }
                val updatedSeats = currentState.seats.map {
                    if (it.id == seat.id) it.copy(status = SeatStatus.AVAILABLE) else it
                }
                currentState.copy(seats = updatedSeats, selectedSeats = newSelectedSeats, errorMessage = null)
            }
            viewModelScope.launch {
                try {
                    RetrofitClient.apiService.unlockSeat(seatIdLong, stId)
                } catch (_: Exception) { }
            }
        }
    }

    fun unlockAllSelectedSeats() {
        val stId = showtimeId?.toLongOrNull() ?: return
        val selectedIds = _state.value.selectedSeats.mapNotNull { it.id.toLongOrNull() }
        if (selectedIds.isEmpty()) return
        viewModelScope.launch {
            selectedIds.forEach { seatId ->
                try { RetrofitClient.apiService.unlockSeat(seatId, stId) } catch (_: Exception) { }
            }
        }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    /** Reload sơ đồ ghế từ server (dùng khi ghế hiển thị sai sau test hoặc thay đổi) */
    fun refreshSeats() {
        _state.update { it.copy(selectedSeats = emptyList()) }
        loadSeats()
    }

    fun getTotalAmount(): Double {
        val s = _state.value
        return (s.regularTicketsCount * s.basePrice) + (s.coupleTicketsCount * s.basePrice * 2)
    }
}
