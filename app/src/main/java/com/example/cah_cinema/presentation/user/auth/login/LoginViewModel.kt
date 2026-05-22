package com.example.cah_cinema.presentation.user.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.GoogleLoginRequest
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

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(email.trim(), password))
                handleLoginResponse(response.isSuccessful, response.body(), response.errorBody()?.string(), response.code(), onSuccess)
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun loginWithGoogle(idToken: String, onSuccess: (String) -> Unit) {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.googleLogin(GoogleLoginRequest(idToken))
                handleLoginResponse(response.isSuccessful, response.body(), response.errorBody()?.string(), response.code(), onSuccess)
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối Google: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun handleLoginResponse(
        isSuccessful: Boolean,
        apiResponse: com.example.cah_cinema.data.model.BaseResponse<com.example.cah_cinema.data.model.LoginData>?,
        errorBody: String?,
        code: Int,
        onSuccess: (String) -> Unit
    ) {
        if (isSuccessful) {
            if (apiResponse != null && apiResponse.code == 200 && apiResponse.data != null) {
                val loginData = apiResponse.data
                RetrofitClient.setToken(loginData.accessToken)
                val role = loginData.user?.role ?: "ROLE_USER"
                onSuccess(role)
            } else {
                errorMessage = apiResponse?.message ?: "Đăng nhập thất bại"
            }
        } else {
            errorMessage = when (code) {
                401 -> "Email hoặc mật khẩu không chính xác"
                403 -> "Tài khoản bị khóa hoặc không có quyền truy cập"
                else -> "Lỗi server ($code): $errorBody"
            }
        }
    }
}
