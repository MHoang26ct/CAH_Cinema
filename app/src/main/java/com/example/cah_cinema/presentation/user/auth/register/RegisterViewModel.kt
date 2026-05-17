package com.example.cah_cinema.presentation.user.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.RegisterRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
        private set

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
    }

    fun register(onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Vui lòng nhập đầy đủ thông tin"
            return
        }

        if (password != confirmPassword) {
            errorMessage = "Mật khẩu xác nhận không khớp"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    name = name
                )
                val response = RetrofitClient.apiService.register(request)
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.code == 200) {
                        onSuccess()
                    } else {
                        errorMessage = apiResponse?.message ?: "Đăng ký thất bại"
                    }
                } else {
                    errorMessage = "Đăng ký thất bại. Email có thể đã tồn tại."
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
