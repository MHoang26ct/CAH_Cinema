package com.example.cah_cinema.presentation.admin.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.BusinessOverviewResponse
import com.example.cah_cinema.data.model.CinemaRevenueResponse
import com.example.cah_cinema.data.model.DailyRevenueResponse
import com.example.cah_cinema.data.model.MovieRevenueResponse
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AdminReportState(
    val businessOverview: BusinessOverviewResponse? = null,
    val dailyRevenue: List<DailyRevenueResponse> = emptyList(),
    val movieRevenue: List<MovieRevenueResponse> = emptyList(),
    val cinemaRevenue: List<CinemaRevenueResponse> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminReportViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminReportState())
    val state: StateFlow<AdminReportState> = _state.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val toDate = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -30) // Last 30 days
        val fromDate = sdf.format(calendar.time)

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                val overview = repository.getBusinessOverview(fromDate, toDate)
                val daily = repository.getDailyRevenue(fromDate, toDate)
                val movies = repository.getMovieRevenue(fromDate, toDate)
                val cinemas = repository.getCinemaRevenue(fromDate, toDate)

                _state.update { it.copy(
                    businessOverview = overview?.data,
                    dailyRevenue = daily?.data ?: emptyList(),
                    movieRevenue = movies?.data ?: emptyList(),
                    cinemaRevenue = cinemas?.data ?: emptyList(),
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
