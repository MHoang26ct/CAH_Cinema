package com.example.cah_cinema.presentation.user.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.TicketType
import com.example.cah_cinema.data.model.MovieDetail
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketSelectionState(
    val movie: Movie? = null,
    val selectedCinemaName: String = "",
    val selectedRoom: String = "",
    val selectedShowtime: String = "",
    val availableShowtimes: List<String> = emptyList(),
    val selectedDate: String = "",
    val ticketTypes: List<TicketType> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TicketSelectionViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(TicketSelectionState())
    val state: StateFlow<TicketSelectionState> = _state.asStateFlow()

    private val movieId: String? = savedStateHandle["movieId"]
    private val showtimeId: String? = savedStateHandle["showtimeId"]
    private val dateArg: String = savedStateHandle["date"] ?: ""
    private val timeArg: String = savedStateHandle["time"] ?: ""

    // basePrice từ ShowtimeInfo — được load từ API showtime detail
    private var currentBasePrice: Double = 0.0

    init {
        _state.update {
            it.copy(
                selectedShowtime = timeArg,
                selectedDate = DateTimeUtils.navDateToDisplay(dateArg)
            )
        }
        loadData()
    }

    fun getBasePrice(): Double = currentBasePrice

    private fun loadData() {
        val mId = movieId?.toLongOrNull() ?: return
        val stId = showtimeId?.toLongOrNull() ?: return

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // Gọi song song: chi tiết phim + showtimes theo ngày để lấy basePrice và roomName
                val movieResponse = RetrofitClient.apiService.getMovieDetail(mId)

                val movieDetail = if (movieResponse.isSuccessful) movieResponse.body()?.data else null

                if (movieDetail == null) {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải thông tin phim") }
                    return@launch
                }

                val context = loadBookingShowtimeContext(mId, stId, dateArg)
                currentBasePrice = context.basePrice

                _state.update {
                    it.copy(
                        movie = context.movie ?: movieDetail.toDomainMovie(),
                        selectedCinemaName = context.cinemaName,
                        selectedRoom = if (context.roomName.isNotEmpty()) "Phòng: ${context.roomName}" else "",
                        availableShowtimes = context.otherShowtimes,
                        ticketTypes = buildTicketTypes(context.basePrice),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Lỗi kết nối") }
            }
        }
    }

    /**
     * Xây dựng danh sách loại vé dựa trên basePrice từ showtime.
     * Backend không có API "ticket types" riêng — giá được tính từ basePrice.
     * Các loại vé và hệ số nhân giá theo quy tắc nghiệp vụ:
     *   - Đơn Người Lớn: basePrice × 1.0
     *   - Đơn HSSV/U22:  basePrice × 1.0 (giá ưu đãi bằng nhau)
     *   - Đơn Người Cao Tuổi: basePrice × 1.0
     *   - Đôi (Couple):  basePrice × 2.0 (2 ghế đôi)
     */
    private fun buildTicketTypes(basePrice: Double): List<TicketType> {
        val price = if (basePrice > 0) basePrice else 45000.0 // fallback nếu API không trả
        return listOf(
            TicketType("1", "Đơn Người Lớn", price),
            TicketType("2", "Đơn HSSV/U22", price),
            TicketType("3", "Đơn Người Cao Tuổi", price),
            TicketType("4", "Đôi Người Lớn", price * 2)
        )
    }

    private fun MovieDetail.toDomainMovie(): Movie {
        return Movie(
            id = this.id.toString(),
            title = this.title,
            genre = this.genres.joinToString(", ") { it.name },
            posterUrl = this.posterUrl,
            age = this.ageRating,
            duration = "${this.duration} phút",
            format = "2D"
        )
    }

    fun onShowtimeChange(newShowtime: String) {
        _state.update { it.copy(selectedShowtime = newShowtime) }
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
            currentState.copy(ticketTypes = updatedTickets)
        }
    }

    fun getTotalAmount(): Double {
        return _state.value.ticketTypes.sumOf { it.price * it.quantity }
    }

    fun getTotalTickets(): Int {
        return _state.value.ticketTypes.sumOf { it.quantity }
    }
}
