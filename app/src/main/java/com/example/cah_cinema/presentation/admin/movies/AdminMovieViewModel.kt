package com.example.cah_cinema.presentation.admin.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.Genre
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.data.model.UpdateOrCreateMovieRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.repository.MovieRepository
import com.example.cah_cinema.data.repository.MovieRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminMovieState(
    val movies: List<MovieListItem> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminMovieViewModel(
    private val movieRepository: MovieRepository = MovieRepositoryImpl(),
    private val adminRepository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminMovieState())
    val state: StateFlow<AdminMovieState> = _state.asStateFlow()

    init {
        loadMovies()
        loadGenres()
    }

    fun loadMovies() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                // We need to update MovieRepository to return MovieListItem for admin if needed, 
                // but for now let's keep it simple or update the API service call here.
                val response = RetrofitClient.apiService.getMovies()
                if (response.isSuccessful) {
                    _state.update { it.copy(movies = response.body()?.data?.content ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Lỗi tải phim") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun loadGenres() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllGenres()
                if (response.isSuccessful) {
                    _state.update { it.copy(genres = response.body()?.data ?: emptyList()) }
                }
            } catch (e: Exception) { }
        }
    }

    fun createMovie(request: UpdateOrCreateMovieRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = adminRepository.createMovie(request)
                if (response?.code == 200) {
                    loadMovies()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi thêm phim") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateMovie(id: Long, request: UpdateOrCreateMovieRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = adminRepository.updateMovie(id, request)
                if (response?.code == 200) {
                    loadMovies()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi cập nhật phim") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteMovie(id: Long) {
        viewModelScope.launch {
            try {
                val response = adminRepository.deleteMovie(id)
                if (response?.code == 200) {
                    loadMovies()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
