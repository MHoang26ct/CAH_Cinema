package com.example.cah_cinema.presentation.admin.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.Genre
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.data.model.UpdateOrCreateMovieRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.repository.AdminRepository
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminMovieState(
    val movies: List<MovieListItem> = listOf(
        MovieListItem(1, "HẸN EM NGÀY NHẬT THỰC", 118, "T16", "https://api.chieu.online/images/poster1.jpg"),
        MovieListItem(2, "KUNG FU PANDA 4", 94, "P", "https://api.chieu.online/images/poster2.jpg"),
        MovieListItem(3, "MA DA", 105, "T18", "https://api.chieu.online/images/poster3.jpg"),
        MovieListItem(4, "DUNE: HÀNH TINH CÁT 2", 166, "T13", "https://api.chieu.online/images/poster4.jpg")
    ),
    val genres: List<Genre> = listOf(
        Genre(1, "Hành Động"), Genre(2, "Kinh Dị"), Genre(3, "Hài"), 
        Genre(4, "Tâm Lý"), Genre(5, "Hoạt Hình"), Genre(6, "Viễn Tưởng")
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminMovieViewModel(
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
                val response = adminRepository.getMovies()
                if (response?.code == 200) {
                    _state.update { it.copy(movies = response.data?.content ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tải phim") }
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
