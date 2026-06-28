package com.example.cah_cinema.presentation.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.BusinessOverviewResponse
import com.example.cah_cinema.data.model.CinemaRevenueResponse
import com.example.cah_cinema.data.model.MovieRevenueResponse
import com.example.cah_cinema.domain.repository.AdminRepository
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AdminDashboardViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminDashboardState())
    val state: StateFlow<AdminDashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val overviewResponse = repository.getBusinessOverview(today, today)
                val movieResponse = repository.getMovieRevenue(today, today)
                val cinemaResponse = repository.getCinemaRevenue(today, today)

                _state.update { 
                    it.copy(
                        overview = overviewResponse?.data,
                        movieRevenue = movieResponse?.data ?: emptyList(),
                        cinemaRevenue = cinemaResponse?.data ?: emptyList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}
