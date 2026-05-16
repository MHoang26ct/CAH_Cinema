package com.example.cah_cinema.presentation.user.promotion

import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Promotion
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PromotionState(
    val promotions: List<Promotion> = emptyList(),
    val isLoading: Boolean = false
)

class PromotionViewModel : ViewModel() {
    private val _state = MutableStateFlow(PromotionState())
    val state: StateFlow<PromotionState> = _state.asStateFlow()

    init {
        loadPromotions()
    }

    private fun loadPromotions() {
        val mockPromotions = listOf(
            Promotion(
                id = "1",
                title = "C'SCHOOL | ƯU ĐÃI GIÁ VÉ TỪ 45K DÀNH RIÊNG CHO HSSV/U22/GIÁO VIÊN",
                description = "Ưu đãi giá vé 45K dành cho HSSV, U22 và Giáo viên cả tuần",
                imageUrl = ImageUrls.PROMOTION_SCHOOL
            ),
            Promotion(
                id = "2",
                title = "HAPPY DAY | THỨ 2 - ĐỒNG GIÁ 45K CHO MỌI SUẤT CHIẾU",
                description = "Thứ 2 hàng tuần, đồng giá vé 45K / vé 2D cho mọi khách hàng tại Cinestar",
                imageUrl = ImageUrls.PROMOTION_HAPPY_DAY
            )
        )
        _state.update { it.copy(promotions = mockPromotions) }
    }
}
