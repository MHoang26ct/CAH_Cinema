package com.example.cah_cinema.presentation.user.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.local.TokenManager
import com.example.cah_cinema.data.model.ChangePasswordRequest
import com.example.cah_cinema.data.model.UpdateProfileRequest
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TicketInfo(
    val movieTitle: String,
    val cinemaName: String,
    val showTime: String,
    val seat: String,
    val posterUrl: String,
    val bookingId: Long = 0,
)

data class ProfileState(
    val userName: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val loyaltyPoints: Int = 0,
    val rank: String = "",
    val role: String = "ROLE_USER",
    val recentTicket: TicketInfo? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class ProfileEvent {
    object Logout : ProfileEvent()
    object ChangePassword : ProfileEvent()
    object DeleteAccount : ProfileEvent()
    object EditProfile : ProfileEvent()
    object ViewAllTickets : ProfileEvent()
    object ViewTicketDetail : ProfileEvent()
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = TokenManager(application)
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        val currentToken = RetrofitClient.getToken()
        if (currentToken.isNullOrBlank()) {
            _state.update { it.copy(isLoading = false, errorMessage = "Vui lòng đăng nhập") }
            return
        }

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyProfile()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val profileData = apiResponse?.data
                    if (profileData != null) {
                        val user = profileData.user
                        val recentInvoice = profileData.recentInvoices.firstOrNull()
                        
                        _state.update { it.copy(
                            userName = user.name,
                            email = user.email,
                            phone = user.phone ?: "",
                            avatarUrl = user.avatarUrl ?: ImageUrls.MOCK_AVATAR,
                            loyaltyPoints = user.totalPoint,
                            rank = user.rankLevel,
                            role = user.role,
                            recentTicket = recentInvoice?.let { invoice ->
                                TicketInfo(
                                    movieTitle = invoice.movieTitle,
                                    cinemaName = invoice.cinemaName,
                                    showTime = invoice.startTime,
                                    seat = "",
                                    posterUrl = invoice.moviePosterUrl,
                                    bookingId = invoice.bookingId
                                )
                            },
                            isLoading = false
                        ) }
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải hồ sơ") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.Logout -> {
                RetrofitClient.setToken(null)
                tokenManager.clearToken()
            }
            ProfileEvent.ChangePassword -> {
            }
            ProfileEvent.DeleteAccount -> {
            }
            ProfileEvent.EditProfile -> {
            }
            ProfileEvent.ViewAllTickets -> {
            }
            ProfileEvent.ViewTicketDetail -> {
            }
        }
    }

    fun updateRecentTicket(ticket: TicketInfo) {
        _state.update { it.copy(recentTicket = ticket) }
    }

    fun updateProfile(name: String, email: String, phone: String, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateMyProfile(UpdateProfileRequest(name, email, phone))
                if (response.isSuccessful) {
                    loadProfileData()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Cập nhật thất bại") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun changePassword(old: String, new: String, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changePassword(ChangePasswordRequest(old, new))
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Đổi mật khẩu thất bại") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
