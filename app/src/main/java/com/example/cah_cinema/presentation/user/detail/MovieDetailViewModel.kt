package com.example.cah_cinema.presentation.user.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.MovieDate
import com.example.cah_cinema.data.model.MovieDetail
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailState(
    val movie: Movie? = null,
    val availableDates: List<MovieDate> = emptyList(),
    val cinemas: List<Cinema> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class MovieDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    private val movieId: String? = savedStateHandle["movieId"]

    init {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        val id = movieId?.toLongOrNull() ?: return
        
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMovieDetail(id)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val movieDetail = apiResponse?.data
                    if (movieDetail != null) {
                        _state.update { it.copy(
                            movie = movieDetail.toDomainMovie(),
                            isLoading = false
                        ) }
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải chi tiết phim") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun MovieDetail.toDomainMovie(): Movie {
        return Movie(
            id = this.id.toString(),
            title = this.title,
            genre = this.genres.joinToString(", ") { it.name },
            posterUrl = this.posterUrl,
            bannerUrl = this.posterUrl, // Tạm thời dùng poster làm banner
            duration = "${this.duration} phút",
            age = this.ageRating,
            director = this.directorName ?: "",
            cast = this.actorList ?: "",
            description = this.description,
            format = "2D",
            trailerUrl = this.trailerUrl
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
    }
}
