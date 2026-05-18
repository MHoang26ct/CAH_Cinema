package com.example.cah_cinema.presentation.user.cinema

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.MovieDate
import com.example.cah_cinema.domain.model.Showtime
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.data.model.CinemaShowtimeItem
import com.example.cah_cinema.data.model.ShowtimeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CinemaDetailState(
    val cinemaName: String = "",
    val availableDates: List<MovieDate> = emptyList(),
    val moviesWithShowtimes: List<Pair<Movie, List<Showtime>>> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class CinemaDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(CinemaDetailState())
    val state: StateFlow<CinemaDetailState> = _state.asStateFlow()

    private val cinemaId: String? = savedStateHandle["cinemaId"]

    init {
        setupDates()
        loadCinemaShowtimes()
    }

    private fun setupDates() {
        val calendar = Calendar.getInstance()
        val dayFormatter = SimpleDateFormat("EE", Locale("vi", "VN"))
        val dateFormatter = SimpleDateFormat("dd/MM", Locale.getDefault())
        
        val dates = (0..6).map { i ->
            val date = calendar.time
            val isToday = i == 0
            val movieDate = MovieDate(
                dayOfWeek = if (isToday) "Hôm nay" else dayFormatter.format(date),
                date = dateFormatter.format(date),
                isSelected = isToday
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            movieDate
        }
        _state.update { it.copy(availableDates = dates) }
    }

    private fun loadCinemaShowtimes() {
        val id = cinemaId?.toLongOrNull() ?: return
        val selectedDate = _state.value.availableDates.find { it.isSelected }?.date ?: ""
        
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        // date có dạng "dd/MM" từ setupDates()
        val parts = selectedDate.split("/")
        val apiDate = if (parts.size == 2) "$currentYear-${parts[1]}-${parts[0]}" else ""

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getShowtimesByCinema(id, apiDate)
                if (response.isSuccessful) {
                    val showtimeItems = response.body()?.data ?: emptyList()
                    
                    val moviesWithShowtimes = showtimeItems.map { item ->
                        toDomainMovie(item) to item.showtimes.map { s -> toDomainShowtime(s) }
                    }
                    _state.update { it.copy(moviesWithShowtimes = moviesWithShowtimes, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải lịch chiếu") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun toDomainMovie(item: CinemaShowtimeItem): Movie {
        return Movie(
            id = item.movie.id.toString(),
            title = item.movie.title,
            genre = "",
            posterUrl = item.movie.posterUrl ?: "",
            format = "",
            age = item.movie.ageRating ?: "P"
        )
    }

    private fun toDomainShowtime(info: ShowtimeInfo): Showtime {
        val parts = info.startTime.split("T")
        val timeStr = if (parts.size == 2) parts[1].substring(0, 5) else ""
        return Showtime(
            id = info.id.toString(),
            time = timeStr
        )
    }

    fun onDateSelected(selectedDate: MovieDate) {
        _state.update { currentState ->
            currentState.copy(
                availableDates = currentState.availableDates.map {
                    it.copy(isSelected = it.date == selectedDate.date)
                }
            )
        }
        loadCinemaShowtimes()
    }
}
