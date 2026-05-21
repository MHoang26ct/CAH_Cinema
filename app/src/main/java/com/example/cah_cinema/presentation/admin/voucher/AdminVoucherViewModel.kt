package com.example.cah_cinema.presentation.admin.voucher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.CreateVoucherRequest
import com.example.cah_cinema.data.model.UpdateVoucherRequest
import com.example.cah_cinema.data.model.VoucherItem
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminVoucherState(
    val vouchers: List<VoucherItem> = listOf(
        VoucherItem(1, "SUMMER20", "FIXED_AMOUNT", 20000.0, 100000.0, null, "31/12/2026"),
        VoucherItem(2, "CINE50", "PERCENT", 50.0, 300000.0, 100000.0, "01/06/2026"),
        VoucherItem(3, "STUDENT", "FIXED_AMOUNT", 15000.0, 50000.0, null, "31/08/2026")
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminVoucherViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminVoucherState())
    val state: StateFlow<AdminVoucherState> = _state.asStateFlow()

    init {
        loadVouchers()
    }

    fun loadVouchers() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = repository.getAllVouchers(0)
                if (response?.code == 200) {
                    _state.update { it.copy(vouchers = response.data ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tải voucher") }
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
                val response = repository.createVoucher(request)
                if (response?.code == 200) {
                    loadVouchers()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tạo voucher") }
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
                val response = repository.updateVoucher(request)
                if (response?.code == 200) {
                    loadVouchers()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi cập nhật voucher") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteVoucher(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.deleteVoucher(id)
                if (response?.code == 200) {
                    loadVouchers()
                }
            } catch (e: Exception) {}
        }
    }
}
