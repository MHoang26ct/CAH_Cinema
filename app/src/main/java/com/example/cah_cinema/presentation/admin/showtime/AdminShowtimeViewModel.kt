package com.example.cah_cinema.presentation.admin.showtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AdminShowtimeState(
    val showtimesByMovie: List<CinemaShowtimeItem> = listOf(
        CinemaShowtimeItem(
            movie = MovieInfo(1, "HẸN EM NGÀY NHẬT THỰC", "", "T16"),
            showtimes = listOf(
                ShowtimeInfo(1001, "2026-05-16T18:20:00", "2026-05-16T20:18:00", "2D", 45000.0, "AVAILABLE", "Phòng 01"),
                ShowtimeInfo(1002, "2026-05-16T20:30:00", "2026-05-16T22:28:00", "2D", 45000.0, "AVAILABLE", "Phòng 01")
            )
        ),
        CinemaShowtimeItem(
            movie = MovieInfo(2, "KUNG FU PANDA 4", "", "P"),
            showtimes = listOf(
                ShowtimeInfo(2001, "2026-05-16T10:00:00", "2026-05-16T11:34:00", "3D", 65000.0, "AVAILABLE", "Phòng 02")
            )
        )
    ),
    val movies: List<MovieListItem> = emptyList(),
    val cinemas: List<CinemaItem> = emptyList(),
    val rooms: List<RoomItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminShowtimeViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminShowtimeState())
    val state: StateFlow<AdminShowtimeState> = _state.asStateFlow()

    init {
        loadShowtimes()
        loadMovies()
        loadCinemas()
    }

    fun loadShowtimes() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // For admin, maybe show all showtimes for a selected cinema
                val cinemaId = _state.value.cinemas.firstOrNull()?.id ?: 7L // Default to Landmark 81 if empty
                val response = repository.getShowtimesByCinema(cinemaId, today) 
                if (response?.code == 200) {
                    _state.update { it.copy(showtimesByMovie = response.data ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tải lịch chiếu") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMovies().body()
                if (response?.code == 200) {
                    _state.update { it.copy(movies = response.data?.content ?: emptyList()) }
                }
            } catch (e: Exception) {}
        }
    }

    private fun loadCinemas() {
        viewModelScope.launch {
            try {
                val response = repository.getCinemas()
                if (response?.code == 200) {
                    _state.update { it.copy(cinemas = response.data ?: emptyList()) }
                }
            } catch (e: Exception) {}
        }
    }

    fun loadRooms(cinemaId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.getRoomsByCinema(cinemaId)
                if (response?.code == 200) {
                    _state.update { it.copy(rooms = response.data ?: emptyList()) }
                }
            } catch (e: Exception) {}
        }
    }

    fun createShowtime(request: CreateShowtimeRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = repository.createShowtime(request)
                if (response?.code == 200) {
                    loadShowtimes()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tạo lịch chiếu") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteShowtime(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.deleteShowtime(id)
                if (response?.code == 200) {
                    loadShowtimes()
                }
            } catch (e: Exception) { }
        }
    }

    fun updateShowtime(request: UpdateShowtimeRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = repository.updateShowtime(request)
                if (response?.code == 200) {
                    loadShowtimes()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi cập nhật lịch chiếu") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
