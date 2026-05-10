package com.example.cah_cinema.presentation.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.Seat
import com.example.cah_cinema.domain.model.SeatStatus
import com.example.cah_cinema.domain.model.SeatType
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    private val dateArg: String = savedStateHandle["date"] ?: "06/04"
    private val timeArg: String = savedStateHandle["time"] ?: "18:20"
    private val regularCount: Int = savedStateHandle.get<Int>("regularCount") ?: 0
    private val coupleCount: Int = savedStateHandle.get<Int>("coupleCount") ?: 0
    private val basePrice: Long = savedStateHandle.get<Long>("basePrice") ?: 0L

    init {
        _state.update { it.copy(
            regularTicketsCount = regularCount,
            coupleTicketsCount = coupleCount,
            basePrice = basePrice.toDouble()
        ) }
        loadData()
    }

    private fun loadData() {
        val mockMovie = Movie(
            id = "hen_em_ngay_nhat_thuc",
            title = "HẸN EM NGÀY NHẬT THỰC",
            genre = "Drama",
            posterUrl = ImageUrls.HEN_EM_NGAY_NHAT_THUC_BANNER,
            format = "2D",
            age = "T13",
            duration = "118 phút"
        )

        val mockSeats = mutableListOf<Seat>()
        val rows = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")
        
        for (row in rows) {
            val count = if (row == "L") 6 else 16
            for (i in 1..count) {
                val type = when {
                    row == "L" -> SeatType.COUPLE
                    row in listOf("F", "G", "H", "I", "J", "K") -> SeatType.VIP
                    else -> SeatType.REGULAR
                }
                
                val status = when {
                    (row == "B") && (i == 6) -> SeatStatus.TAKEN_BY_OTHERS
                    (row == "C") && (i == 14) -> SeatStatus.TAKEN_BY_OTHERS
                    (row == "C") && ((i == 9) || (i == 4)) -> SeatStatus.MAINTENANCE
                    (row == "D") && (i == 11) -> SeatStatus.TAKEN_BY_OTHERS
                    (row == "D") && (i == 7) -> SeatStatus.BOOKED
                    else -> SeatStatus.AVAILABLE
                }
                
                mockSeats.add(
                    Seat(
                        id = "$row$i",
                        row = row,
                        number = i.toString().padStart(2, '0'),
                        type = type,
                        status = status
                    )
                )
            }
        }

        _state.update {
            it.copy(
                movie = mockMovie,
                selectedCinemaName = "Cinestar Quốc Thanh (TP.HCM)",
                selectedRoom = "Phòng: 03",
                selectedShowtime = timeArg,
                selectedDate = dateArg,
                seats = mockSeats
            )
        }
    }

    fun onSeatClick(seat: Seat) {
        // Xử lý các trạng thái không thể chọn
        when (seat.status) {
            SeatStatus.BOOKED -> {
                _state.update { it.copy(errorMessage = "ghế đã được đặt") }
                return
            }
            SeatStatus.TAKEN_BY_OTHERS -> {
                _state.update { it.copy(errorMessage = "ghế đang được chọn bởi người khác") }
                return
            }
            SeatStatus.MAINTENANCE -> {
                _state.update { it.copy(errorMessage = "ghế đang bảo trì") }
                return
            }
            else -> {} // AVAILABLE hoặc SELECTED thì tiếp tục xử lý
        }

        _state.update { currentState ->
            val isSelected = currentState.selectedSeats.any { it.id == seat.id }
            
            if (!isSelected) {
                // 1. Kiểm tra tổng số lượng ghế đã chọn có vượt quá tổng số vé không
                val totalTicketsAllowed = currentState.regularTicketsCount + currentState.coupleTicketsCount
                if (currentState.selectedSeats.size >= totalTicketsAllowed) return@update currentState

                // 2. Ràng buộc: Vé đôi chỉ được chọn ghế đôi
                // Nếu người dùng chọn ghế không phải ghế đôi, nhưng đã hết suất vé thường (phải dùng vé đôi)
                if (seat.type != SeatType.COUPLE) {
                    val currentNonCoupleSelected = currentState.selectedSeats.count { it.type != SeatType.COUPLE }
                    if (currentNonCoupleSelected >= currentState.regularTicketsCount) {
                        return@update currentState.copy(errorMessage = "vui lòng chọn ghế đôi")
                    }
                }
                
                val newSelectedSeats = currentState.selectedSeats + seat
                val updatedSeats = currentState.seats.map {
                    if (it.id == seat.id) it.copy(status = SeatStatus.SELECTED) else it
                }
                
                currentState.copy(
                    seats = updatedSeats,
                    selectedSeats = newSelectedSeats,
                    extraPrice = calculateExtraPrice(newSelectedSeats, currentState.coupleTicketsCount),
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
                    extraPrice = calculateExtraPrice(newSelectedSeats, currentState.coupleTicketsCount),
                    errorMessage = null
                )
            }
        }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun calculateExtraPrice(selectedSeats: List<Seat>, coupleTicketsCount: Int): Double {
        var extra = 0.0
        
        // Phụ phí ghế VIP: +30k/ghế
        val vipCount = selectedSeats.count { it.type == SeatType.VIP }
        extra += vipCount * 30000.0
        
        // Phụ phí ghế đôi: +50k/ghế nếu không có vé đôi tương ứng
        val selectedCoupleSeatsCount = selectedSeats.count { it.type == SeatType.COUPLE }
        val extraCoupleSeats = (selectedCoupleSeatsCount - coupleTicketsCount).coerceAtLeast(0)
        extra += extraCoupleSeats * 50000.0
        
        return extra
    }

    fun getTotalAmount(): Double {
        return _state.value.basePrice + _state.value.extraPrice
    }
}
