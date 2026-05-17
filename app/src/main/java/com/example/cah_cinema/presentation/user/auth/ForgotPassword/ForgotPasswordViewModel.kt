package com.example.cah_cinema.presentation.user.auth.ForgotPassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.OtpRequest
import com.example.cah_cinema.data.model.OtpVerifyRequest
import com.example.cah_cinema.data.model.ResetPasswordRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {
    var email by mutableStateOf("")
    var otp by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var resetToken by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var step by mutableStateOf(1) // 1: Email, 2: OTP, 3: New Password

    fun onEmailChange(value: String) { email = value }
    fun onOtpChange(value: String) { otp = value }
    fun onNewPasswordChange(value: String) { newPassword = value }

    fun sendOtp(onSuccess: () -> Unit) {
        if (email.isBlank()) {
            errorMessage = "Vui lòng nhập email"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.sendOtp(OtpRequest(email))
                if (response.isSuccessful && response.body()?.code == 200) {
                    step = 2
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Gửi OTP thất bại"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun verifyOtp(onSuccess: (String) -> Unit) {
        if (otp.length != 6) {
            errorMessage = "OTP phải có 6 chữ số"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.verifyForgotPasswordOtp(OtpVerifyRequest(email, otp))
                if (response.isSuccessful && response.body()?.code == 200) {
                    val token = response.body()?.data?.token ?: ""
                    resetToken = token
                    step = 3
                    onSuccess(token)
                } else {
                    errorMessage = response.body()?.message ?: "OTP không hợp lệ"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun resetPassword(onSuccess: () -> Unit) {
        if (newPassword.isBlank()) {
            errorMessage = "Vui lòng nhập mật khẩu mới"
            return
        }
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changeForgotPassword(
                    ResetPasswordRequest(email, newPassword, resetToken)
                )
                if (response.isSuccessful && response.body()?.code == 200) {
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Đổi mật khẩu thất bại"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
