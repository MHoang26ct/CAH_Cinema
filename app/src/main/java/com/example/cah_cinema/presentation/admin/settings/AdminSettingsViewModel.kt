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
    val errorMessage: String? = null
)

class AdminSettingsViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminSettingsState())
    val state: StateFlow<AdminSettingsState> = _state.asStateFlow()

    init {
        loadData()
        loadMockData()
    }

    fun loadData() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val prices = repository.getAllPriceConfigs()
                val holidays = repository.getAllHolidays()
                
                _state.update { it.copy(
                    priceConfigs = prices?.data ?: it.priceConfigs,
                    holidays = holidays?.data ?: it.holidays,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun loadMockData() {
        _state.update { it.copy(
            priceConfigs = listOf(
                PriceConfig(1, 1.0, "WEEKDAY", "MORNING", "2D"),
                PriceConfig(2, 1.2, "WEEKEND", "EVENING", "2D"),
                PriceConfig(3, 1.5, "HOLIDAY", "EVENING", "IMAX")
            ),
            holidays = listOf(
                Holiday(1, "2026-01-01", "Tết Dương Lịch", true),
                Holiday(2, "2026-04-30", "Giải phóng Miền Nam", true),
                Holiday(3, "2026-05-01", "Quốc tế Lao động", true)
            )
        ) }
    }

    fun updatePriceConfig(config: PriceConfig) {
        viewModelScope.launch {
            try {
                repository.updatePriceConfig(config)
                loadData()
            } catch (e: Exception) {}
        }
    }

    fun createHoliday(date: String, name: String, isRecurring: Boolean) {
        viewModelScope.launch {
            try {
                repository.createHoliday(Holiday(date = date, name = name, isRecurring = isRecurring))
                loadData()
            } catch (e: Exception) {}
        }
    }

    fun deleteHoliday(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteHoliday(id)
                loadData()
            } catch (e: Exception) {}
        }
    }
}
