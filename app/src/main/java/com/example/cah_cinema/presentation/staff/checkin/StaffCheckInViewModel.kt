package com.example.cah_cinema.presentation.staff.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.CheckInResponse
import com.example.cah_cinema.data.repository.StaffRepositoryImpl
import com.example.cah_cinema.domain.repository.StaffRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CheckInUiState {
    object Idle : CheckInUiState()
    object Scanning : CheckInUiState()
    object Loading : CheckInUiState()
    data class Success(val result: CheckInResponse) : CheckInUiState()
    data class Error(val message: String) : CheckInUiState()
}

class StaffCheckInViewModel(
    private val repository: StaffRepository = StaffRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckInUiState>(CheckInUiState.Idle)
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    /** Gọi khi camera scan được QR code hoặc nhập tay */
    fun checkIn(token: String) {
        if (_uiState.value is CheckInUiState.Loading) return

        android.util.Log.d("CheckIn", "Scanned token: $token")

        viewModelScope.launch {
            _uiState.value = CheckInUiState.Loading
            repository.checkIn(token)
                .onSuccess { response ->
                    _uiState.value = CheckInUiState.Success(response)
                }
                .onFailure { error ->
                    _uiState.value = CheckInUiState.Error(
                        error.message ?: "Check-in thất bại"
                    )
                }
        }
    }

    /** Gọi khi camera scan được QR code */
    fun onQrCodeScanned(rawValue: String) {
        checkIn(rawValue)
    }

    /** Quay lại trạng thái quét sau khi xem kết quả */
    fun resetToScanning() {
        _uiState.value = CheckInUiState.Scanning
    }

    /** Bắt đầu quét */
    fun startScanning() {
        _uiState.value = CheckInUiState.Scanning
    }

    /** Reset hoàn toàn */
    fun reset() {
        _uiState.value = CheckInUiState.Idle
    }
}
