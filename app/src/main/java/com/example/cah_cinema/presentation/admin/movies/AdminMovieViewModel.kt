package com.example.cah_cinema.presentation.admin.movies

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.Genre
import com.example.cah_cinema.data.model.MovieDetail
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.data.model.UpdateOrCreateMovieRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.repository.AdminRepository
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.util.CloudinaryUploader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminMovieState(
    val movies: List<MovieListItem> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
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

    fun loadGenres() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getAllGenres()
                if (response.isSuccessful) {
                    _state.update { it.copy(genres = response.body()?.data ?: emptyList()) }
                }
            } catch (_: Exception) { }
        }
    }

    /**
     * Upload ảnh poster lên Cloudinary, trả về URL qua callback.
     */
    fun uploadPosterImage(context: Context, imageUri: Uri, onResult: (String?) -> Unit) {
        _state.update { it.copy(isUploading = true) }
        viewModelScope.launch {
            val result = CloudinaryUploader.uploadImage(context, imageUri)
            _state.update { it.copy(isUploading = false) }
            result.fold(
                onSuccess = { url -> onResult(url) },
                onFailure = {
                    _state.update { s -> s.copy(errorMessage = "Upload ảnh thất bại: ${it.message}") }
                    onResult(null)
                }
            )
        }
    }

    fun createMovie(request: UpdateOrCreateMovieRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = adminRepository.createMovie(request)
                if (response?.code == 200) {
                    _state.update { it.copy(successMessage = "Thêm phim thành công") }
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
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = adminRepository.updateMovie(id, request)
                if (response?.code == 200) {
                    _state.update { it.copy(successMessage = "Cập nhật phim thành công") }
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
                } else {
                    _state.update { it.copy(errorMessage = "Xóa phim thất bại") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
