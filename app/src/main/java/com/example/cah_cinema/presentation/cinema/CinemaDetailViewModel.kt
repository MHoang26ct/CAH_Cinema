package com.example.cah_cinema.presentation.cinema

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.MovieDate
import com.example.cah_cinema.domain.model.Showtime
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CinemaDetailState(
    val cinemaName: String = "",
    val availableDates: List<MovieDate> = emptyList(),
    val moviesWithShowtimes: List<Pair<Movie, List<Showtime>>> = emptyList(),
    val isLoading: Boolean = false
)

class CinemaDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(CinemaDetailState())
    val state: StateFlow<CinemaDetailState> = _state.asStateFlow()

    private val cinemaId: String? = savedStateHandle["cinemaId"]

    init {
        loadCinemaDetails()
    }

    /**
     * BACKEND NOTE:
     * - Fetch cinema name, movies and their showtimes for this cinemaId from Repository.
     * - Use the selected date to filter showtimes.
     */
    private fun loadCinemaDetails() {
        // Mock data matching the image
        val mockDates = listOf(
            MovieDate("Hôm nay", "06/04", true),
            MovieDate("Thứ 3", "07/04"),
            MovieDate("Thứ 4", "08/04"),
            MovieDate("Thứ 5", "09/04"),
            MovieDate("Thứ 6", "10/04"),
            MovieDate("Thứ 7", "11/04")
        )

        val mockMovies = listOf(
            Movie(
                id = "hen_em_ngay_nhat_thuc",
                title = "HẸN EM NGÀY NHẬT THỰC",
                genre = "Drama",
                posterUrl = ImageUrls.HEN_EM_NGAY_NHAT_THUC_BANNER,
                format = "2D",
                age = "T16"
            ) to listOf(
                Showtime("s1", "18:20"),
                Showtime("s2", "20:00"),
                Showtime("s3", "20:10")
            ),
            Movie(
                id = "bay_tien",
                title = "BẪY TIÊN",
                genre = "Drama",
                posterUrl = ImageUrls.HEN_EM_NGAY_NHAT_THUC_BANNER, // Replace with real poster
                format = "2D",
                age = "T16"
            ) to listOf(
                Showtime("s4", "18:20"),
                Showtime("s5", "20:00"),
                Showtime("s6", "20:10")
            ),
            Movie(
                id = "bay_tien_2",
                title = "BẪY TIÊN",
                genre = "Drama",
                posterUrl = ImageUrls.HEN_EM_NGAY_NHAT_THUC_BANNER,
                format = "2D",
                age = "T16"
            ) to listOf(
                Showtime("s7", "18:20"),
                Showtime("s8", "20:00"),
                Showtime("s9", "20:10")
            )
        )

        _state.update {
            it.copy(
                cinemaName = if (cinemaId == "1") "Cinestar sinh viên (Bình Dương)" else "Cinestar Quốc Thanh",
                availableDates = mockDates,
                moviesWithShowtimes = mockMovies
            )
        }
    }

    fun onDateSelected(selectedDate: MovieDate) {
        _state.update { currentState ->
            currentState.copy(
                availableDates = currentState.availableDates.map {
                    it.copy(isSelected = it.date == selectedDate.date)
                }
            )
        }
        // BACKEND NOTE: Reload showtimes for the new date
    }
}
