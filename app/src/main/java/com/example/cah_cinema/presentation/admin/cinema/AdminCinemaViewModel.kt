package com.example.cah_cinema.presentation.admin.cinema

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.CinemaItem
import com.example.cah_cinema.data.model.CreateCinemaRequest
import com.example.cah_cinema.data.model.CreateRoomRequest
import com.example.cah_cinema.data.model.RoomItem
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminCinemaState(
    val cinemas: List<CinemaItem> = listOf(
        CinemaItem(1, "Cinestar Quốc Thanh", "271 Nguyễn Trãi, Q.1, TP.HCM", "028 7300 8881"),
        CinemaItem(2, "Cinestar Hai Bà Trưng", "233 Hai Bà Trưng, Q.3, TP.HCM", "028 7300 7279"),
        CinemaItem(3, "Cinestar Sinh Viên", "Nhà văn hóa Sinh viên ĐHQG, Thủ Đức", "028 7300 1122"),
        CinemaItem(4, "Cinestar Lâm Đồng", "Quảng trường Hòa Bình, Đà Lạt", "0263 7300 888")
    ),
    val roomsByCinema: Map<Long, List<RoomItem>> = mapOf(
        1L to listOf(RoomItem(101, 1, "Phòng 01"), RoomItem(102, 1, "Phòng 02"), RoomItem(103, 1, "Phòng 03")),
        2L to listOf(RoomItem(201, 2, "Phòng Gold"), RoomItem(202, 2, "Phòng Silver"))
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminCinemaViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminCinemaState())
    val state: StateFlow<AdminCinemaState> = _state.asStateFlow()

    init {
        loadCinemas()
        loadMockData()
    }

    private fun loadMockData() {
        _state.update { currentState ->
            currentState.copy(
                roomsByCinema = mapOf(
                    1L to listOf(
                        RoomItem(101, 1, "Phòng 01 - Standard"), 
                        RoomItem(102, 1, "Phòng 02 - IMAX"), 
                        RoomItem(103, 1, "Phòng Deluxe")
                    ),
                    2L to listOf(
                        RoomItem(201, 2, "Hall 01 (Gold)"), 
                        RoomItem(202, 2, "Hall 02 (Silver)")
                    ),
                    3L to listOf(
                        RoomItem(301, 3, "Phòng SV 01"),
                        RoomItem(302, 3, "Phòng SV 02"),
                        RoomItem(303, 3, "Sân khấu kịch")
                    )
                )
            )
        }
    }

    fun loadCinemas() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = repository.getCinemas()
                if (response?.code == 200) {
                    val cinemas = response.data ?: emptyList()
                    _state.update { it.copy(cinemas = cinemas, isLoading = false) }
                    
                    // Load rooms for each cinema
                    cinemas.forEach { cinema ->
                        loadRooms(cinema.id)
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tải rạp") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun createCinema(name: String, address: String, hotline: String, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val request = CreateCinemaRequest(name, address, hotline)
                val response = repository.createCinema(request)
                if (response?.code == 200) {
                    loadCinemas()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tạo rạp") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteCinema(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.deleteCinema(id)
                if (response?.code == 200) {
                    loadCinemas()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun createRoom(cinemaId: Long, roomName: String, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = repository.createRoom(cinemaId, CreateRoomRequest(cinemaId, roomName))
                if (response?.code == 200) {
                    loadRooms(cinemaId)
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tạo phòng") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun loadRooms(cinemaId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.getRoomsByCinema(cinemaId)
                if (response?.code == 200) {
                    _state.update { currentState ->
                        val newRoomsMap = currentState.roomsByCinema.toMutableMap()
                        newRoomsMap[cinemaId] = response.data ?: emptyList()
                        currentState.copy(roomsByCinema = newRoomsMap)
                    }
                }
            } catch (e: Exception) {}
        }
    }
}
