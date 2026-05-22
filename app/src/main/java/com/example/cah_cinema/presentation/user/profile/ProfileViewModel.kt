package com.example.cah_cinema.presentation.user.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.Invoice
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class TicketInfo(
    val movieTitle: String,
    val cinemaName: String,
    val showTime: String,
    val seat: String,
    val posterUrl: String,
    val bookingId: Long = 0,
    val roomName: String = "",
    val totalPrice: Double = 0.0,
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
    // Tất cả invoices từ API (tối đa 5 từ /users/me)
    val allInvoices: List<Invoice> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDeleteConfirm: Boolean = false
)

sealed class ProfileEvent {
    object Logout : ProfileEvent()
    object ChangePassword : ProfileEvent()
    object DeleteAccount : ProfileEvent()
    object ConfirmDeleteAccount : ProfileEvent()
    object CancelDeleteAccount : ProfileEvent()
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
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyProfile()
                if (response.isSuccessful) {
                    val profileData = response.body()?.data
                    if (profileData != null) {
                        val user = profileData.user
                        val invoices = profileData.recentInvoices
                        val recentInvoice = invoices.firstOrNull()

                        _state.update {
                            it.copy(
                                userName = user.name,
                                email = user.email,
                                phone = user.phone ?: "",
                                avatarUrl = user.avatarUrl ?: ImageUrls.MOCK_AVATAR,
                                loyaltyPoints = user.totalPoint,
                                rank = translateRank(user.rankLevel),
                                role = user.role,
                                allInvoices = invoices,
                                recentTicket = recentInvoice?.let { invoice ->
                                    val seatDisplay = invoice.seats
                                        ?.map { s ->
                                            val row = ('A' + (s.seatRow.toInt() - 1)).toString()
                                            val col = s.seatCol.toInt().toString().padStart(2, '0')
                                            "$row$col"
                                        }
                                        ?.joinToString(", ") ?: ""
                                    TicketInfo(
                                        movieTitle = invoice.movieTitle,
                                        cinemaName = invoice.cinemaName,
                                        showTime = formatDateTime(invoice.startTime),
                                        seat = seatDisplay,
                                        posterUrl = invoice.moviePosterUrl,
                                        bookingId = invoice.bookingId,
                                        roomName = invoice.roomName ?: "",
                                        totalPrice = invoice.totalPrice
                                    )
                                },
                                isLoading = false
                            )
                        }
                    } else {
                        _state.update { it.copy(isLoading = false) }
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
            ProfileEvent.DeleteAccount -> {
                // Hiện dialog xác nhận
                _state.update { it.copy(showDeleteConfirm = true) }
            }
            ProfileEvent.ConfirmDeleteAccount -> {
                _state.update { it.copy(showDeleteConfirm = false) }
                // Backend chưa có API xóa tài khoản — chỉ logout
                RetrofitClient.setToken(null)
            }
            ProfileEvent.CancelDeleteAccount -> {
                _state.update { it.copy(showDeleteConfirm = false) }
            }
            else -> { /* Handled in UI layer via callbacks */ }
        }
    }

    fun updateRecentTicket(ticket: TicketInfo) {
        _state.update { it.copy(recentTicket = ticket) }
    }

    fun setSelectedInvoice(invoice: Invoice) {
        val seatDisplay = invoice.seats
            ?.map { s ->
                val row = ('A' + (s.seatRow.toInt() - 1)).toString()
                val col = s.seatCol.toInt().toString().padStart(2, '0')
                "$row$col"
            }
            ?.joinToString(", ") ?: ""
        _state.update {
            it.copy(
                recentTicket = TicketInfo(
                    movieTitle = invoice.movieTitle,
                    cinemaName = invoice.cinemaName,
                    showTime = formatDateTime(invoice.startTime),
                    seat = seatDisplay,
                    posterUrl = invoice.moviePosterUrl,
                    bookingId = invoice.bookingId,
                    roomName = invoice.roomName ?: "",
                    totalPrice = invoice.totalPrice
                )
            )
        }
    }

    private fun formatDateTime(isoString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(isoString)
            if (date != null) outputFormat.format(date) else isoString
        } catch (e: Exception) {
            isoString
        }
    }

    private fun translateRank(rank: String): String {
        return when (rank.uppercase()) {
            "SILVER" -> "Hạng Bạc"
            "GOLD" -> "Hạng Vàng"
            "DIAMOND" -> "Hạng Kim Cương"
            else -> rank
        }
    }

    fun clearErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }
}
