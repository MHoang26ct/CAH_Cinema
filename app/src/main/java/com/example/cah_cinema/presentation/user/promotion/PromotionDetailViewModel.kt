package com.example.cah_cinema.presentation.user.promotion

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.model.Promotion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PromotionDetailState(
    val promotion: Promotion? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PromotionDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PromotionDetailState())
    val state: StateFlow<PromotionDetailState> = _state.asStateFlow()

    private val promotionId: String? = savedStateHandle["promotionId"]

    init {
        loadPromotionDetail()
    }

    private fun loadPromotionDetail() {
        val idLong = promotionId?.toLongOrNull() ?: return
        
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            try {
                // Dùng API Public: chỉ trả promotion isActive = true
                val response = RetrofitClient.apiService.getPublicPromotionDetail(idLong)
                if (response.isSuccessful) {
                    val detail: com.example.cah_cinema.data.model.AdminPromotionDetail? = response.body()?.data
                    if (detail != null) {
                        // Tách chuỗi theo ký tự '@' theo logic backend
                        val conditionsList = detail.conditions?.split("@")?.filter { it.isNotBlank() } ?: emptyList()
                        val notesList = detail.notes?.split("@")?.filter { it.isNotBlank() } ?: emptyList()

                        val domainPromotion = Promotion(
                            id = detail.id.toString(),
                            title = detail.title,
                            description = detail.description ?: "",
                            imageUrl = detail.imageUrl ?: "",
                            conditions = conditionsList,
                            notes = notesList
                        )
                        _state.update { it.copy(promotion = domainPromotion, isLoading = false) }
                    } else {
                        _state.update { it.copy(isLoading = false, errorMessage = "Không tìm thấy dữ liệu") }
                    }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Lỗi tải chi tiết khuyến mãi") }
                }
            } catch (e: Exception) {
                Log.e("PromotionDetailVM", "Error", e)
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Lỗi kết nối, vui lòng thử lại") }
            }
        }
    }
}
