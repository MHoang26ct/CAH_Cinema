package com.example.cah_cinema.presentation.promotion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Promotion
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PromotionDetailState(
    val promotion: Promotion? = null,
    val isLoading: Boolean = false
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
        // BACKEND NOTE: Fetch detailed promotion by id from Repository/API
        if (promotionId == "1") {
            val mockPromotion = Promotion(
                id = "1",
                title = "C'SCHOOL | ƯU ĐÃI GIÁ VÉ TỪ 45K DÀNH RIÊNG CHO HSSV/U22/GIÁO VIÊN",
                description = "Ưu đãi giá vé 45K dành cho HSSV, U22 và Giáo viên cả tuần",
                imageUrl = ImageUrls.PROMOTION_SCHOOL,
                conditions = listOf(
                    "Giá vé ưu đãi 45.000đ/ vé 2D áp dụng vào: Thứ 2 và các suất chiếu trước 10h00;",
                    "Giá vé ưu đãi 49.000đ/ vé 2D áp dụng tất cả các suất chiếu còn lại;",
                    "Giá vé ưu đãi 55.000đ/ vé 3D áp dụng tất cả các suất chiếu trước 10h00 và sau 22h00;",
                    "Giá vé ưu đãi 95.000đ/ C'MÊ (giường nằm) áp dụng vào: thứ 2 và các suất chiếu trước 10h00 (Thứ 3, 4, 5) tại Cinestar Quốc Thanh, Cinestar Satra Quận 6.",
                    "Giá vé ưu đãi 99.000đ/ C'MÊ (giường nằm) áp dụng vào: thứ 3, 4, 5 tại Cinestar Quốc Thanh, Cinestar Satra Quận 6"
                ),
                notes = listOf(
                    "Khách hàng là Học sinh Sinh viên đang mặc đồng phục trường học hoặc xuất trình thẻ Học sinh sinh viên / hình ảnh thẻ Học sinh Sinh viên chính chủ có thể xác minh;",
                    "Khách hàng từ 22 tuổi trở xuống (có năm sinh từ 2003 trở lên) có thể xuất trình Căn cước/ VNeID / hình ảnh Căn cước chính chủ hoặc giấy tờ tùy thân có thể xác minh như Bằng lái xe, ...;",
                    "Khách hàng là giáo viên/ giảng viên xuất trình chứng chỉ sư phạm, hoặc thẻ giáo viên / giảng viên hoặc hình ảnh thẻ giáo viên/giảng viên chính chủ có thể xác minh.",
                    "Mỗi thẻ HSSV/U22/GV, khách hàng chỉ mua được 01 vé trên 01 suất chiếu;",
                    "Học sinh Sinh viên/ U22 có thể đặt vé với giá ưu đãi tại app Cinestar hoặc web cinestar.com.vn;",
                    "Học sinh Sinh viên/ U22 có thể áp dụng giá vé ưu đãi đối với các suất chiếu sớm/ suất chiếu đặc biệt tùy theo quy định từ nhà phát hành phim."
                )
            )
            _state.update { it.copy(promotion = mockPromotion) }
        }
    }
}
