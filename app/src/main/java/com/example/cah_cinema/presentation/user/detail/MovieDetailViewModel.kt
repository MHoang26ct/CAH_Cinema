package com.example.cah_cinema.presentation.user.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.repository.MovieRepositoryImpl
import com.example.cah_cinema.domain.model.CinemaWithShowtimes
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.MovieDate
import com.example.cah_cinema.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailState(
    val movie: Movie? = null,
    val availableDates: List<MovieDate> = emptyList(),
    val cinemas: List<CinemaWithShowtimes> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class MovieDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository = MovieRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    private val movieIdStr: String? = savedStateHandle["movieId"]

    init {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        val id = movieIdStr?.toLongOrNull() ?: return
        
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            repository.getMovieDetail(id).onSuccess { movie ->
                _state.update { it.copy(movie = movie, isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.message ?: "Không thể tải chi tiết phim") }
            }
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
    }
}
