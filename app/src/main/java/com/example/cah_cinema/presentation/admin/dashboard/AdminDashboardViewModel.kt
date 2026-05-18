package com.example.cah_cinema.presentation.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.BusinessOverviewResponse
import com.example.cah_cinema.domain.repository.AdminRepository
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AdminDashboardState(
    val overview: BusinessOverviewResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminDashboardViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminDashboardState())
    val state: StateFlow<AdminDashboardState> = _state.asStateFlow()

    init {
        loadOverview()
    }

    fun loadOverview() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val toDate = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val fromDate = sdf.format(calendar.time)
        
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.getBusinessOverview(fromDate, toDate)
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(overview = resp.data, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tải báo cáo") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
