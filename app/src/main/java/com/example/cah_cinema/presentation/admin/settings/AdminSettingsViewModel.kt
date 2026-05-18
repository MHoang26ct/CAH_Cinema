package com.example.cah_cinema.presentation.admin.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.Holiday
import com.example.cah_cinema.data.model.PriceConfig
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminSettingsState(
    val priceConfigs: List<PriceConfig> = emptyList(),
    val holidays: List<Holiday> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminSettingsViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminSettingsState())
    val state: StateFlow<AdminSettingsState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        android.util.Log.d("AdminSettings", "Loading data...")
        viewModelScope.launch {
            try {
                val prices = repository.getAllPriceConfigs()
                val holidays = repository.getAllHolidays()
                android.util.Log.d("AdminSettings", "Prices: ${prices?.data?.size}, Holidays: ${holidays?.data?.size}")
                _state.update { it.copy(
                    priceConfigs = prices?.data ?: emptyList(),
                    holidays = holidays?.data ?: emptyList(),
                    isLoading = false
                ) }
            } catch (e: Exception) {
                android.util.Log.e("AdminSettings", "Load error: ${e.message}", e)
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updatePriceConfig(config: PriceConfig) {
        viewModelScope.launch {
            try {
                val response = repository.updatePriceConfig(config)
                if (response?.code in 200..299) {
                    _state.update { it.copy(successMessage = "Cập nhật giá thành công") }
                    loadData()
                } else {
                    _state.update { it.copy(errorMessage = response?.message ?: "Lỗi cập nhật giá") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun createHoliday(date: String, name: String, isRecurring: Boolean) {
        viewModelScope.launch {
            try {
                val response = repository.createHoliday(Holiday(date = date, name = name, isRecurring = isRecurring))
                if (response?.code in 200..299) {
                    _state.update { it.copy(successMessage = "Thêm ngày lễ thành công") }
                    loadData()
                } else {
                    _state.update { it.copy(errorMessage = response?.message ?: "Lỗi thêm ngày lễ") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun deleteHoliday(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.deleteHoliday(id)
                if (response?.code in 200..299) {
                    loadData()
                } else {
                    _state.update { it.copy(errorMessage = response?.message ?: "Lỗi xóa ngày lễ") }
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
