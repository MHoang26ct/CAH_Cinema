package com.example.cah_cinema.presentation.user.promotion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.domain.model.Promotion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PromotionState(
    val promotions: List<Promotion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PromotionViewModel : ViewModel() {
    private val _state = MutableStateFlow(PromotionState())
    val state: StateFlow<PromotionState> = _state.asStateFlow()

    init {
        loadPromotions()
    }

    private fun loadPromotions() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getPublicPromotions(page = 0, size = 9)
                if (response.isSuccessful) {
                    val body = response.body()
                    val promos = body?.data?.content ?: emptyList<com.example.cah_cinema.data.model.AdminPromotionItem>()
                    val domainPromos = promos.map { item ->
                        Promotion(
                            id = item.id.toString(),
                            title = item.title,
                            description = item.description ?: "",
                            imageUrl = item.imageUrl ?: ""
                        )
                    }
                    _state.update { it.copy(promotions = domainPromos, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải khuyến mãi") }
                }
            } catch (e: Exception) {
                Log.e("PromotionViewModel", "Error loading promotions", e)
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Lỗi kết nối, vui lòng thử lại") }
            }
        }
    }
}
