package com.example.cah_cinema.presentation.staff.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StaffDashboardViewModel : ViewModel() {

    private val _staffName = MutableStateFlow("Nhân viên")
    val staffName: StateFlow<String> = _staffName.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyProfile()
                if (response.isSuccessful) {
                    val name = response.body()?.data?.user?.name ?: "Nhân viên"
                    _staffName.value = name
                }
            } catch (e: Exception) {
                // Giữ tên mặc định
            }
        }
    }

    fun logout() {
        RetrofitClient.setToken(null)
    }
}
