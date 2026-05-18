package com.example.cah_cinema.presentation.admin.showtime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.*
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
    val showtimesByMovie: List<CinemaShowtimeItem> = emptyList(),
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
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val moviesJob = launch { loadMovies() }
            val cinemasJob = launch { loadCinemas() }
            moviesJob.join()
            cinemasJob.join()
            loadShowtimes()
        }
    }

    fun loadShowtimes() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        val cinemas = _state.value.cinemas
        
        if (cinemas.isEmpty()) {
            _state.update { it.copy(isLoading = false) }
            return
        }
        
        val cinemaId = cinemas.first().id
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.getShowtimesByCinema(cinemaId, today)
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(showtimesByMovie = resp.data ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tải lịch chiếu") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private suspend fun loadMovies() {
        try {
            val resp = repository.getMovies()
            if (resp != null && resp.code in 200..299) {
                _state.update { it.copy(movies = resp.data?.content ?: emptyList()) }
            }
        } catch (e: Exception) {}
    }

    private suspend fun loadCinemas() {
        try {
            val resp = repository.getCinemas()
            if (resp != null && resp.code in 200..299) {
                _state.update { it.copy(cinemas = resp.data ?: emptyList()) }
            }
        } catch (e: Exception) {}
    }

    fun loadRooms(cinemaId: Long) {
        viewModelScope.launch {
            try {
                val resp = repository.getRoomsByCinema(cinemaId)
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(rooms = resp.data ?: emptyList()) }
                }
            } catch (e: Exception) {}
        }
    }

    fun createShowtime(request: CreateShowtimeRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        android.util.Log.d("AdminShowtime", "Creating showtime: $request")
        viewModelScope.launch {
            try {
                val resp = repository.createShowtime(request)
                android.util.Log.d("AdminShowtime", "Response: $resp")
                if (resp != null && resp.code in 200..299) {
                    loadShowtimes()
                    onSuccess()
                } else {
                    val msg = resp?.message ?: "Lỗi tạo lịch chiếu"
                    android.util.Log.e("AdminShowtime", "Error: $msg")
                    _state.update { it.copy(isLoading = false, errorMessage = msg) }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminShowtime", "Exception: ${e.message}", e)
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateShowtime(request: UpdateShowtimeRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val resp = repository.updateShowtime(request)
                if (resp != null && resp.code in 200..299) {
                    loadShowtimes()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi cập nhật lịch chiếu") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteShowtime(id: Long) {
        viewModelScope.launch {
            try {
                val resp = repository.deleteShowtime(id)
                if (resp != null && resp.code in 200..299) {
                    loadShowtimes()
                }
            } catch (e: Exception) { }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}
