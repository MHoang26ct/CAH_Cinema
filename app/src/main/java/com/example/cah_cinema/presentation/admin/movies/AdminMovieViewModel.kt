package com.example.cah_cinema.presentation.admin.movies

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.Genre
import com.example.cah_cinema.data.model.MovieDetail
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.data.model.UpdateOrCreateMovieRequest
import com.example.cah_cinema.data.model.BaseResponse
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
    val editingMovieDetail: MovieDetail? = null,
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val isUploading: Boolean = false,
    val isUploadingVideo: Boolean = false,
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
                val resp = adminRepository.getMovies()
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(movies = resp.data?.content ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tải phim") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun loadMovieDetail(id: Long) {
        _state.update { it.copy(isLoadingDetail = true, editingMovieDetail = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMovieDetail(id)
                if (response.isSuccessful) {
                    _state.update { it.copy(editingMovieDetail = response.body()?.data, isLoadingDetail = false) }
                } else {
                    _state.update { it.copy(isLoadingDetail = false, errorMessage = "Không thể lấy chi tiết phim") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingDetail = false, errorMessage = "Lỗi kết nối detail: ${e.message}") }
            }
        }
    }

    fun clearEditingDetail() {
        _state.update { it.copy(editingMovieDetail = null) }
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

    fun uploadTrailerVideo(context: Context, videoUri: Uri, onResult: (String?) -> Unit) {
        _state.update { it.copy(isUploadingVideo = true) }
        viewModelScope.launch {
            val result = CloudinaryUploader.uploadVideo(context, videoUri)
            _state.update { it.copy(isUploadingVideo = false) }
            result.fold(
                onSuccess = { url -> onResult(url) },
                onFailure = {
                    _state.update { s -> s.copy(errorMessage = "Upload video thất bại: ${it.message}") }
                    onResult(null)
                }
            )
        }
    }

    fun createMovie(request: UpdateOrCreateMovieRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = adminRepository.createMovie(request)
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(successMessage = "Thêm phim thành công") }
                    onSuccess()
                    loadMovies()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi thêm phim") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun updateMovie(id: Long, request: UpdateOrCreateMovieRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = adminRepository.updateMovie(id, request)
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(successMessage = "Cập nhật phim thành công") }
                    onSuccess()
                    loadMovies()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi cập nhật phim") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun deleteMovie(id: Long) {
        viewModelScope.launch {
            try {
                val resp = adminRepository.deleteMovie(id)
                if (resp != null && resp.code in 200..299) {
                    loadMovies()
                } else {
                    _state.update { it.copy(errorMessage = resp?.message ?: "Xóa phim thất bại") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
