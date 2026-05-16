package com.example.cah_cinema.presentation.admin.seats

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.CreateSeatRequest
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminSeatMapState(
    val roomId: Long = 0,
    val selectedSeats: Set<Pair<Int, Int>> = emptySet(), // row, col
    val seatTypes: Map<Pair<Int, Int>, Long> = emptyMap(), // 1: Regular, 2: VIP, 3: Couple
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminSeatManagementViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val roomId: Long = savedStateHandle["roomId"] ?: 0L
    
    private val _state = MutableStateFlow(AdminSeatMapState(roomId = roomId))
    val state: StateFlow<AdminSeatMapState> = _state.asStateFlow()

    init {
        loadExistingLayout()
    }

    private fun loadExistingLayout() {
        // If it's a specific room, pre-load a mock layout to show it works
        if (roomId == 101L || roomId == 201L || roomId == 301L) {
            _state.update { currentState ->
                val newSelected = mutableSetOf<Pair<Int, Int>>()
                val newTypes = mutableMapOf<Pair<Int, Int>, Long>()
                
                // Mock a simple rectangular layout
                for (r in 3..8) {
                    for (c in 2..11) {
                        val pos = r to c
                        newSelected.add(pos)
                        newTypes[pos] = if (r >= 7) 2L else 1L // VIP for back rows
                    }
                }
                // Couple seats at the very back
                for (c in 4..9 step 2) {
                    val pos = 9 to c
                    newSelected.add(pos)
                    newTypes[pos] = 3L
                }

                currentState.copy(selectedSeats = newSelected, seatTypes = newTypes)
            }
        }
    }

    fun toggleSeat(row: Int, col: Int, seatTypeId: Long) {
        val pos = row to col
        _state.update { currentState ->
            val newSelected = currentState.selectedSeats.toMutableSet()
            val newTypes = currentState.seatTypes.toMutableMap()
            
            if (newSelected.contains(pos)) {
                if (newTypes[pos] == seatTypeId) {
                    newSelected.remove(pos)
                    newTypes.remove(pos)
                } else {
                    newTypes[pos] = seatTypeId
                }
            } else {
                newSelected.add(pos)
                newTypes[pos] = seatTypeId
            }
            
            currentState.copy(selectedSeats = newSelected, seatTypes = newTypes)
        }
    }

    fun saveSeatMap(onSuccess: () -> Unit) {
        val seatRequests = _state.value.selectedSeats.map { pos ->
            CreateSeatRequest(
                roomId = roomId,
                row = pos.first.toDouble(),
                col = pos.second.toDouble(),
                seatTypeId = _state.value.seatTypes[pos] ?: 1L
            )
        }

        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // Delete existing first
                repository.deleteSeatsByRoom(roomId)
                
                // Create new
                val response = repository.createSeats(seatRequests)
                if (response?.code == 200) {
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi lưu sơ đồ") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
