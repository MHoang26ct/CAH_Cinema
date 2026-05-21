package com.example.cah_cinema.presentation.user.auth.login

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
import com.example.cah_cinema.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val loginUseCase: LoginUseCase = LoginUseCase(AuthRepositoryImpl())
) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)

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
            loginUseCase(email, password).onSuccess { result ->
                val accessToken = result.accessToken
                RetrofitClient.setToken(accessToken)
                tokenManager.saveToken(accessToken)
                
                val role = result.user.role
                onSuccess(role)
                isLoading = false
            }.onFailure { error ->
                errorMessage = error.message ?: "Đăng nhập thất bại"
                isLoading = false
            }
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
