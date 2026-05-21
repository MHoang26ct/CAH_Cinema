package com.example.cah_cinema.presentation.user.auth.register

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cah_cinema.data.local.TokenManager
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.data.repository.AuthRepositoryImpl
import com.example.cah_cinema.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(
    application: Application,
    private val registerUseCase: RegisterUseCase = RegisterUseCase(AuthRepositoryImpl())
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    
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
            registerUseCase(email, password, name, null).onSuccess { result ->
                val accessToken = result.accessToken
                RetrofitClient.setToken(accessToken)
                tokenManager.saveToken(accessToken)
                onSuccess()
                isLoading = false
            }.onFailure { error ->
                errorMessage = error.message ?: "Đăng ký thất bại"
                isLoading = false
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegisterViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
