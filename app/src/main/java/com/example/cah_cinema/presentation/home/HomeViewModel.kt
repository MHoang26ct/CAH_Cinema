package com.example.cah_cinema.presentation.home

import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.Promotion
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeState(
    val userName: String = "Hinno",
    val featuredMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val promotions: List<Promotion> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {
    
    private val dummyFeatured = listOf(
        Movie(
            id = "hen_em_ngay_nhat_thuc",
            title = "Hẹn Em Ngày Nhật Thực",
            genre = "Tình cảm",
            posterUrl = ImageUrls.HEN_EM_NGAY_NHAT_THUC_POSTER,
            bannerUrl = ImageUrls.HEN_EM_NGAY_NHAT_THUC_BANNER,
            format = "2D",
            age = "T16",
            duration = "118 phút"
        ),
        Movie(
            id = "4",
            title = "Kung Fu Panda 4",
            genre = "Hoạt hình/Hành động",
            posterUrl = ImageUrls.KUNG_FU_PANDA_4_POSTER,
            format = "3D",
            age = "P"
        )
    )

    private val dummyUpcoming = listOf(
        Movie(
            id = "2",
            title = "Captain America 4",
            genre = "Hành động/Viễn tưởng",
            posterUrl = ImageUrls.CAPTAIN_AMERICA_4_POSTER,
            releaseDate = "14/02/2025",
            isUpcoming = true
        ),
        Movie(
            id = "3",
            title = "Superman",
            genre = "Hành động/Viễn tưởng",
            posterUrl = ImageUrls.SUPERMAN_POSTER,
            releaseDate = "11/07/2025",
            isUpcoming = true
        )
    )

    private val dummyPromotions = listOf(
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

    private val _state = MutableStateFlow(
        HomeState(
            userName = "Hinno",
            featuredMovies = dummyFeatured,
            upcomingMovies = dummyUpcoming,
            promotions = dummyPromotions
        )
    )
    val state: StateFlow<HomeState> = _state.asStateFlow()
}
