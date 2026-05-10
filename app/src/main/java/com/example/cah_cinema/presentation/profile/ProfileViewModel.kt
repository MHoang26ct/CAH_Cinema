package com.example.cah_cinema.presentation.profile

import androidx.lifecycle.ViewModel
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TicketInfo(
    val movieTitle: String,
    val cinemaName: String,
    val showTime: String,
    val seat: String,
    val posterUrl: String
)

data class ProfileState(
    val userName: String = "",
    val email: String = "",
    val avatarUrl: String = "",
    val loyaltyPoints: Int = 0,
    val rank: String = "",
    val recentTicket: TicketInfo? = null,
    val isLoading: Boolean = false
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

    private fun loadProfileData() {
        // TODO: Backend integration - Fetch user profile and recent ticket
        // Note: Loyalty points = Total spent / 40,000
        val mockTotalSpent = 54680000L 
        val mockLoyaltyPoints = (mockTotalSpent / 40000).toInt()

        _state.value = ProfileState(
            userName = "Hinno",
            email = "example@gmail.com",
            avatarUrl = ImageUrls.MOCK_AVATAR,
            loyaltyPoints = mockLoyaltyPoints,
            rank = "Hạng vàng",
            recentTicket = TicketInfo(
                movieTitle = "HẸN EM NGÀY NHẬT THỰC (T13)",
                cinemaName = "Cinestar Quốc Thanh (TP.HCM)",
                showTime = "18:20 - 06/04/2026",
                seat = "A11",
                posterUrl = ImageUrls.MOCK_POSTER
            )
        )
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.Logout -> {
                // TODO: Backend integration - Handle logout
            }
            ProfileEvent.ChangePassword -> {
                // TODO: Navigation to Change Password screen
            }
            ProfileEvent.DeleteAccount -> {
                // TODO: Backend integration - Handle account deletion
            }
            ProfileEvent.EditProfile -> {
                // TODO: Navigation to Edit Profile screen
            }
            ProfileEvent.ViewAllTickets -> {
                // TODO: Navigation to Ticket List screen
            }
            ProfileEvent.ViewTicketDetail -> {
                // TODO: Navigation to Ticket Detail screen
            }
        }
    }

    fun updateRecentTicket(ticket: TicketInfo) {
        _state.update { it.copy(recentTicket = ticket) }
    }
}
