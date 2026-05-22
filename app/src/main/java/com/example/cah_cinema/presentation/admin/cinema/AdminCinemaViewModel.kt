package com.example.cah_cinema.presentation.admin.cinema

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import com.example.cah_cinema.util.CloudinaryUploader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminCinemaState(
    val cinemas: List<CinemaItem> = emptyList(),
    val roomsByCinema: Map<Long, List<RoomItem>> = emptyMap(),
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminCinemaViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminCinemaState())
    val state: StateFlow<AdminCinemaState> = _state.asStateFlow()

    init {
        loadCinemas()
    }

    fun loadCinemas() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.getCinemas()
                if (resp != null && resp.code in 200..299) {
                    val data = resp.data ?: emptyList()
                    _state.update { it.copy(cinemas = data, isLoading = false) }
                    data.forEach { cinema -> loadRooms(cinema.id) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tải rạp") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun uploadImage(context: Context, uri: Uri, onResult: (String?) -> Unit) {
        _state.update { it.copy(isUploading = true) }
        viewModelScope.launch {
            val result = CloudinaryUploader.uploadImage(context, uri)
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

    fun createCinema(name: String, address: String, hotline: String, imageUrl: String? = null, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.createCinema(CreateCinemaRequest(name, address, hotline, imageUrl))
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(successMessage = "Tạo rạp thành công") }
                    onSuccess()
                    loadCinemas()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tạo rạp") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateCinema(cinemaId: Long, name: String, address: String, hotline: String, imageUrl: String? = null, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.updateCinema(cinemaId, CreateCinemaRequest(name, address, hotline, imageUrl))
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(successMessage = "Cập nhật rạp thành công") }
                    onSuccess()
                    loadCinemas()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi cập nhật") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteCinema(id: Long) {
        viewModelScope.launch {
            try {
                val resp = repository.deleteCinema(id)
                if (resp != null && resp.code in 200..299) {
                    loadCinemas()
                } else {
                    _state.update { it.copy(errorMessage = resp?.message ?: "Xóa thất bại") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun createRoom(cinemaId: Long, roomName: String, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.createRoom(cinemaId, CreateRoomRequest(cinemaId, roomName))
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(isLoading = false, successMessage = "Tạo phòng thành công") }
                    onSuccess()
                    loadRooms(cinemaId)
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tạo phòng") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteRoom(roomId: Long, cinemaId: Long) {
        viewModelScope.launch {
            try {
                val resp = repository.deleteRoom(roomId)
                if (resp != null && resp.code in 200..299) {
                    loadRooms(cinemaId)
                } else {
                    _state.update { it.copy(errorMessage = resp?.message ?: "Xóa phòng thất bại") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun loadRooms(cinemaId: Long) {
        viewModelScope.launch {
            try {
                val resp = repository.getRoomsByCinema(cinemaId)
                if (resp != null && resp.code in 200..299) {
                    _state.update { s ->
                        val map = s.roomsByCinema.toMutableMap()
                        map[cinemaId] = resp.data ?: emptyList()
                        s.copy(roomsByCinema = map, isLoading = false)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
