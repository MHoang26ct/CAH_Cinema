package com.example.cah_cinema.presentation.admin.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val dailyRevenue: List<DailyRevenueResponse> = listOf(
        DailyRevenueResponse("2026-05-10", 12000000.0, 150),
        DailyRevenueResponse("2026-05-11", 15000000.0, 180),
        DailyRevenueResponse("2026-05-12", 9000000.0, 110),
        DailyRevenueResponse("2026-05-13", 18000000.0, 220)
    ),
    val movieRevenue: List<MovieRevenueResponse> = listOf(
        MovieRevenueResponse(1, "HẸN EM NGÀY NHẬT THỰC", 45000000.0, 600),
        MovieRevenueResponse(2, "KUNG FU PANDA 4", 32000000.0, 450),
        MovieRevenueResponse(3, "MA DA", 28000000.0, 380)
    ),
    val cinemaRevenue: List<CinemaRevenueResponse> = listOf(
        CinemaRevenueResponse(1, "Cinestar Quốc Thanh", 55000000.0, 750),
        CinemaRevenueResponse(2, "Cinestar Hai Bà Trưng", 48000000.0, 620)
    ),
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
                val daily = repository.getDailyRevenue(fromDate, toDate)
                val movies = repository.getMovieRevenue(fromDate, toDate)
                val cinemas = repository.getCinemaRevenue(fromDate, toDate)

                _state.update { it.copy(
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
