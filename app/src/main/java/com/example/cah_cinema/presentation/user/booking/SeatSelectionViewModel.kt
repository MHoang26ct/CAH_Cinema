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
    private val basePrice: Long = savedStateHandle.get<Long>("basePrice") ?: 0L

    init {
        _state.update { it.copy(
            regularTicketsCount = regularCount,
            coupleTicketsCount = coupleCount,
            basePrice = basePrice.toDouble(),
            selectedShowtime = timeArg,
            selectedDate = dateArg
        ) }
        loadSeats()
    }

    private fun loadSeats() {
        val id = showtimeId?.toLongOrNull() ?: return
        
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getSeats(id)
                if (response.isSuccessful) {
                    val seatItems = response.body()?.data ?: emptyList()
                    val domainSeats = seatItems.map { it.toDomainSeat() }
                    _state.update { it.copy(seats = domainSeats, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải sơ đồ ghế") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun SeatItem.toDomainSeat(): Seat {
        return Seat(
            id = this.id.toString(),
            row = ('A' + (this.row.toInt() - 1)).toString(),
            number = this.col.toInt().toString().padStart(2, '0'),
            type = when(this.type) {
                "COUPLE" -> SeatType.COUPLE
                "VIP" -> SeatType.VIP
                else -> SeatType.REGULAR
            },
            status = when(this.status) {
                "AVAILABLE" -> SeatStatus.AVAILABLE
                "LOCKED", "BOOKED" -> SeatStatus.TAKEN_BY_OTHERS
                else -> SeatStatus.MAINTENANCE
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

        _state.update { currentState ->
            val isSelected = currentState.selectedSeats.any { it.id == seat.id }
            
            if (!isSelected) {
                val totalTicketsAllowed = currentState.regularTicketsCount + currentState.coupleTicketsCount
                if (currentState.selectedSeats.size >= totalTicketsAllowed) return@update currentState
                
                val newSelectedSeats = currentState.selectedSeats + seat
                val updatedSeats = currentState.seats.map {
                    if (it.id == seat.id) it.copy(status = SeatStatus.SELECTED) else it
                }
                
                currentState.copy(
                    seats = updatedSeats,
                    selectedSeats = newSelectedSeats,
                    errorMessage = null
                )
            } else {
                val newSelectedSeats = currentState.selectedSeats.filter { it.id != seat.id }
                val updatedSeats = currentState.seats.map {
                    if (it.id == seat.id) it.copy(status = SeatStatus.AVAILABLE) else it
                }
                
                currentState.copy(
                    seats = updatedSeats,
                    selectedSeats = newSelectedSeats,
                    errorMessage = null
                )
            }
        }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun getTotalAmount(): Double {
        return _state.value.basePrice // Tạm thời dùng basePrice, backend sẽ tính toán khi tạo booking
    }
}
