package com.example.cah_cinema.presentation.user.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.VoucherItem
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VoucherState(
    val vouchers: List<VoucherItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class VoucherViewModel : ViewModel() {
    private val _state = MutableStateFlow(VoucherState())
    val state: StateFlow<VoucherState> = _state.asStateFlow()

    init {
        loadVouchers()
    }

    private fun loadVouchers() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyVouchers()
                if (response.isSuccessful) {
                    _state.update { it.copy(vouchers = response.body()?.data ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải voucher") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
