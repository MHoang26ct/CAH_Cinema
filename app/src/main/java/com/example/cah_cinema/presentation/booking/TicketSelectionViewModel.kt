package com.example.cah_cinema.presentation.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.TicketType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TicketSelectionState(
    val movie: Movie? = null,
    val selectedCinemaName: String = "",
    val selectedRoom: String = "",
    val selectedShowtime: String = "",
    val availableShowtimes: List<String> = emptyList(),
    val selectedDate: String = "",
    val ticketTypes: List<TicketType> = emptyList(),
    val isLoading: Boolean = false
)

class TicketSelectionViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(TicketSelectionState())
    val state: StateFlow<TicketSelectionState> = _state.asStateFlow()

    private val movieId: String? = savedStateHandle["movieId"]
    private val showtimeId: String? = savedStateHandle["showtimeId"]
    private val dateArg: String = savedStateHandle["date"] ?: "06/04"
    private val timeArg: String = savedStateHandle["time"] ?: "18:20"

    init {
        loadBookingDetails()
    }

    private fun loadBookingDetails() {
        val mockMovie = Movie(
            id = "hen_em_ngay_nhat_thuc",
            title = "HẸN EM NGÀY NHẬT THỰC",
            genre = "Drama",
            posterUrl = "https://files.betacinemas.vn/files/media/images/2024/04/16/434863920-1123447998937086-458145417830209700-n-102551-160424-42.jpg",
            format = "2D",
            age = "T16",
            duration = "118 phút"
        )

        val mockTicketTypes = listOf(
            TicketType("1", "Đơn Người Lớn", 45000.0),
            TicketType("2", "Đơn HSSV", 45000.0),
            TicketType("3", "Đơn Người Cao Tuổi", 45000.0),
            TicketType("4", "Đôi Người Lớn", 100000.0)
        )

        val mockShowtimes = listOf("18:20", "18:50", "20:30", "22:15")

        _state.update {
            it.copy(
                movie = mockMovie,
                selectedCinemaName = "Cinestar Quốc Thanh (TP.HCM)",
                selectedRoom = "Phòng: 03",
                selectedShowtime = timeArg,
                availableShowtimes = mockShowtimes,
                selectedDate = dateArg,
                ticketTypes = mockTicketTypes
            )
        }
    }

    fun onShowtimeChange(newShowtime: String) {
        _state.update { 
            it.copy(selectedShowtime = newShowtime)
        }
    }

    fun onQuantityChange(ticketTypeId: String, delta: Int) {
        _state.update { currentState ->
            val updatedTickets = currentState.ticketTypes.map {
                if (it.id == ticketTypeId) {
                    val newQuantity = (it.quantity + delta).coerceAtLeast(0)
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
            
            currentState.copy(
                ticketTypes = updatedTickets
            )
        }
    }

    fun getTotalAmount(): Double {
        return _state.value.ticketTypes.sumOf { it.price * it.quantity }
    }

    fun getTotalTickets(): Int {
        return _state.value.ticketTypes.sumOf { it.quantity }
    }
}
