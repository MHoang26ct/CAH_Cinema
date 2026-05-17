package com.example.cah_cinema.presentation.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class ProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        // MOCK DATA FOR ADMIN TESTING
        if (RetrofitClient.getToken() == "mock_admin_token") {
            _state.update { it.copy(
                userName = "Admin CAH",
                email = "admin@cah.com",
                role = "ROLE_ADMIN",
                rank = "Administrator",
                isLoading = false
            ) }
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
}
