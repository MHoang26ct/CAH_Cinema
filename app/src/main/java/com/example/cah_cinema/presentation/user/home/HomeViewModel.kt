package com.example.cah_cinema.presentation.user.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.repository.MovieRepositoryImpl
import com.example.cah_cinema.data.repository.UserRepositoryImpl
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.Promotion
import com.example.cah_cinema.domain.usecase.GetFeaturedMoviesUseCase
import com.example.cah_cinema.domain.usecase.GetMyProfileUseCase
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val userName: String = "Khách",
    val featuredMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val promotions: List<Promotion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class HomeViewModel(
    // In a real project, we would use Hilt or Koin to inject these
    private val getFeaturedMoviesUseCase: GetFeaturedMoviesUseCase = GetFeaturedMoviesUseCase(MovieRepositoryImpl()),
    private val getMyProfileUseCase: GetMyProfileUseCase = GetMyProfileUseCase(UserRepositoryImpl())
) : ViewModel() {
    
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
            userName = "Khách",
            promotions = dummyPromotions
        )
    )
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        fetchMovies()
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getMyProfileUseCase().onSuccess { user ->
                _state.update { it.copy(userName = user.name, isLoading = false) }
            }.onFailure {
                _state.update { it.copy(userName = "Khách", isLoading = false) }
            }
        }
    }

    private fun fetchMovies() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            getFeaturedMoviesUseCase().onSuccess { featured ->
                _state.update { it.copy(
                    featuredMovies = featured.nowShowing,
                    upcomingMovies = featured.upcoming,
                    isLoading = false
                ) }
            }.onFailure { error ->
                _state.update { it.copy(
                    isLoading = false, 
                    errorMessage = error.message ?: "Không thể tải danh sách phim"
                ) }
            }
        }
    }
}
