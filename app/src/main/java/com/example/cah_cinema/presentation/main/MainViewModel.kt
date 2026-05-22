package com.example.cah_cinema.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _startDestination = MutableStateFlow(Screen.Login.route)
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            val savedToken = RetrofitClient.getToken()
            
            if (!savedToken.isNullOrEmpty()) {
                try {
                    // Thử gọi API profile để kiểm tra token còn hạn không và lấy role
                    val response = RetrofitClient.apiService.getMyProfile()
                    if (response.isSuccessful && response.body()?.code == 200) {
                        val role = response.body()?.data?.user?.role ?: "ROLE_USER"
                        if (role == "ROLE_ADMIN") {
                            _startDestination.value = Screen.AdminDashboard.route
                        } else {
                            _startDestination.value = Screen.Home.route
                        }
                    } else {
                        // Token hết hạn hoặc không hợp lệ
                        RetrofitClient.setToken(null)
                        _startDestination.value = Screen.Login.route
                    }
                } catch (e: Exception) {
                    // Lỗi mạng — vẫn để ở Login cho an toàn hoặc giữ Home nếu muốn offline
                    _startDestination.value = Screen.Login.route
                }
            } else {
                _startDestination.value = Screen.Login.route
            }
            
            _isReady.value = true
        }
    }
}
