package com.example.cah_cinema.presentation.admin.voucher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminVoucherState(
    val vouchers: List<VoucherItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLastPage: Boolean = false,
    val currentPage: Int = 0,
    val errorMessage: String? = null
)

class AdminVoucherViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminVoucherState())
    val state: StateFlow<AdminVoucherState> = _state.asStateFlow()

    init {
        loadVouchers(reset = true)
    }

    fun loadVouchers(reset: Boolean = false) {
        val pageToLoad = if (reset) 0 else _state.value.currentPage + 1
        if (!reset && _state.value.isLastPage) return

        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.getAllVouchers(pageToLoad)
                if (resp != null && resp.code in 200..299) {
                    val slice = resp.data
                    val newVouchers = slice?.content ?: emptyList()
                    _state.update { 
                        it.copy(
                            vouchers = if (reset) newVouchers else it.vouchers + newVouchers,
                            isLoading = false,
                            isLastPage = slice?.last ?: true,
                            currentPage = pageToLoad
                        ) 
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tải voucher") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun createVoucher(request: CreateVoucherRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val resp = repository.createVoucher(request)
                if (resp != null && resp.code in 200..299) {
                    loadVouchers(reset = true)
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tạo voucher") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateVoucher(request: UpdateVoucherRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val resp = repository.updateVoucher(request)
                if (resp != null && resp.code in 200..299) {
                    loadVouchers(reset = true)
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi cập nhật voucher") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteVoucher(voucherId: Long) {
        viewModelScope.launch {
            try {
                val resp = repository.deleteVoucher(voucherId)
                if (resp != null && resp.code in 200..299) {
                    loadVouchers(reset = true)
                } else {
                    _state.update { it.copy(errorMessage = resp?.message ?: "Lỗi xóa voucher") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null) }
    }
}
