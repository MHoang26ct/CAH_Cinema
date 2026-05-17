package com.example.cah_cinema.presentation.user.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.LoginRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
        private set

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun login(onSuccess: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Vui lòng nhập đầy đủ thông tin"
            return
        }

        // MOCK ADMIN LOGIN FOR TESTING
        if (email == "admin@cah.com" && password == "admin123") {
            RetrofitClient.setToken("mock_admin_token")
            onSuccess("ROLE_ADMIN")
            return
        }

        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.code == 200 && apiResponse.data != null) {
                        RetrofitClient.setToken(apiResponse.data.accessToken)
                        val role = apiResponse.data.user?.role ?: "ROLE_USER"
                        onSuccess(role)
                    } else {
                        errorMessage = apiResponse?.message ?: "Đăng nhập thất bại"
                    }
                } else {
                    errorMessage = "Email hoặc mật khẩu không chính xác"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
