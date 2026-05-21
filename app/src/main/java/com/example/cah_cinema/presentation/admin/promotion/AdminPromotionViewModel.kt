package com.example.cah_cinema.presentation.admin.promotion

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.*
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import com.example.cah_cinema.util.CloudinaryUploader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminPromotionState(
    val promotions: List<AdminPromotionItem> = emptyList(),
    val currentPromotionDetail: AdminPromotionDetail? = null,
    val isLoading: Boolean = false,
    val isLoadingDetail: Boolean = false,
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class AdminPromotionViewModel(
    private val adminRepository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminPromotionState())
    val state: StateFlow<AdminPromotionState> = _state.asStateFlow()

    init {
        loadPromotions()
    }

    fun loadPromotions() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = adminRepository.getAdminPromotions(0)
                if (response?.code in 200..299) {
                    _state.update { it.copy(promotions = response?.data?.content ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tải khuyến mãi") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun loadPromotionDetail(id: Long) {
        _state.update { it.copy(isLoadingDetail = true, currentPromotionDetail = null) }
        viewModelScope.launch {
            try {
                val response = adminRepository.getAdminPromotionDetail(id)
                val responseCode = response?.code ?: -1
                if (responseCode in 200..299) {
                    _state.update { it.copy(currentPromotionDetail = response?.data, isLoadingDetail = false) }
                } else {
                    _state.update { it.copy(isLoadingDetail = false, errorMessage = response?.message ?: "Lỗi tải chi tiết") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingDetail = false, errorMessage = "Lỗi kết nối detail: ${e.message}") }
            }
        }
    }

    fun uploadImage(context: Context, uri: Uri, onResult: (String?) -> Unit) {
        _state.update { it.copy(isUploading = true) }
        viewModelScope.launch {
            val result = CloudinaryUploader.uploadImage(context, uri)
            _state.update { it.copy(isUploading = false) }
            result.fold(
                onSuccess = { url -> onResult(url) },
                onFailure = {
                    _state.update { s -> s.copy(errorMessage = "Upload ảnh thất bại: ${it.message}") }
                    onResult(null)
                }
            )
        }
    }

    fun createPromotion(request: CreateOrUpdatePromotionRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = adminRepository.createPromotion(request)
                if (response?.code in 200..299) {
                    _state.update { it.copy(successMessage = "Thêm khuyến mãi thành công") }
                    onSuccess()
                    loadPromotions()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi thêm khuyến mãi") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun updatePromotion(id: Long, request: CreateOrUpdatePromotionRequest, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = adminRepository.updatePromotion(id, request)
                if (response?.code in 200..299) {
                    _state.update { it.copy(successMessage = "Cập nhật khuyến mãi thành công") }
                    onSuccess()
                    loadPromotions()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi cập nhật khuyến mãi") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun deletePromotion(id: Long) {
        viewModelScope.launch {
            try {
                val response = adminRepository.deletePromotion(id)
                if (response?.code in 200..299) {
                    loadPromotions()
                } else {
                    _state.update { it.copy(errorMessage = "Xóa khuyến mãi thất bại: ${response?.message}") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun clearEditingDetail() {
        _state.update { it.copy(currentPromotionDetail = null) }
    }
}
