package com.example.cah_cinema.presentation.user.promotion

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

    // Dữ liệu tĩnh cho tất cả promotions — backend chưa có API promotion
    private val allPromotions = mapOf(
        "1" to Promotion(
            id = "1",
            title = "C'SCHOOL | ƯU ĐÃI GIÁ VÉ TỪ 45K DÀNH RIÊNG CHO HSSV/U22/GIÁO VIÊN",
            description = "Ưu đãi giá vé 45K dành cho HSSV, U22 và Giáo viên cả tuần",
            imageUrl = ImageUrls.PROMOTION_SCHOOL,
            conditions = listOf(
                "Giá vé ưu đãi 45.000đ/ vé 2D áp dụng vào: Thứ 2 và các suất chiếu trước 10h00",
                "Giá vé ưu đãi 49.000đ/ vé 2D áp dụng tất cả các suất chiếu còn lại",
                "Giá vé ưu đãi 55.000đ/ vé 3D áp dụng tất cả các suất chiếu trước 10h00 và sau 22h00",
                "Giá vé ưu đãi 95.000đ/ C'MÊ (giường nằm) áp dụng vào: thứ 2 và các suất chiếu trước 10h00 (Thứ 3, 4, 5) tại Cinestar Quốc Thanh, Cinestar Satra Quận 6",
                "Giá vé ưu đãi 99.000đ/ C'MÊ (giường nằm) áp dụng vào: thứ 3, 4, 5 tại Cinestar Quốc Thanh, Cinestar Satra Quận 6"
            ),
            notes = listOf(
                "Khách hàng là Học sinh Sinh viên đang mặc đồng phục trường học hoặc xuất trình thẻ HSSV chính chủ",
                "Khách hàng từ 22 tuổi trở xuống (có năm sinh từ 2003 trở lên) có thể xuất trình Căn cước/ VNeID",
                "Khách hàng là giáo viên/ giảng viên xuất trình chứng chỉ sư phạm hoặc thẻ giáo viên",
                "Mỗi thẻ HSSV/U22/GV, khách hàng chỉ mua được 01 vé trên 01 suất chiếu",
                "Học sinh Sinh viên/ U22 có thể đặt vé với giá ưu đãi tại app hoặc website"
            )
        ),
        "2" to Promotion(
            id = "2",
            title = "HAPPY DAY | THỨ 2 - ĐỒNG GIÁ 45K CHO MỌI SUẤT CHIẾU",
            description = "Thứ 2 hàng tuần, đồng giá vé 45K / vé 2D cho mọi khách hàng tại Cinestar",
            imageUrl = ImageUrls.PROMOTION_HAPPY_DAY,
            conditions = listOf(
                "Áp dụng vào thứ 2 hàng tuần cho tất cả các suất chiếu 2D",
                "Giá vé 45.000đ / vé 2D áp dụng cho mọi đối tượng khách hàng",
                "Không áp dụng cho các suất chiếu đặc biệt, suất chiếu sớm"
            ),
            notes = listOf(
                "Chương trình áp dụng tại tất cả các rạp Cinestar trên toàn quốc",
                "Không kết hợp với các chương trình khuyến mãi khác",
                "Cinestar có quyền thay đổi hoặc kết thúc chương trình mà không cần thông báo trước"
            )
        )
    )

    init {
        loadPromotionDetail()
    }

    private fun loadPromotionDetail() {
        val promotion = allPromotions[promotionId]
        _state.update { it.copy(promotion = promotion) }
    }
}
