package com.example.cah_cinema.presentation.user.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.Promotion
import com.example.cah_cinema.util.ImageUrls
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val userName: String = "",
    val featuredMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val promotions: List<Promotion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class HomeViewModel : ViewModel() {

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

    private val _state = MutableStateFlow(HomeState(promotions = dummyPromotions))
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        fetchFeaturedMovies()
    }

    fun fetchFeaturedMovies() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getFeaturedMovies()
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    _state.update {
                        it.copy(
                            featuredMovies = data?.nowShowing?.map { m -> m.toDomainMovie() } ?: emptyList(),
                            upcomingMovies = data?.upcoming?.map { m -> m.toDomainMovie() } ?: emptyList(),
                            isLoading = false
                        )
                    }
                } else {
                    // Fallback: dùng getMovies nếu featured thất bại
                    fetchMoviesFallback()
                }
            } catch (e: Exception) {
                fetchMoviesFallback()
            }
        }
    }

    private suspend fun fetchMoviesFallback() {
        try {
            val response = RetrofitClient.apiService.getMovies()
            if (response.isSuccessful) {
                val movies = response.body()?.data?.content?.map { it.toDomainMovie() } ?: emptyList()
                _state.update {
                    it.copy(
                        featuredMovies = movies.take(5),
                        upcomingMovies = if (movies.size > 5) movies.drop(5) else emptyList(),
                        isLoading = false
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải danh sách phim") }
            }
        } catch (e: Exception) {
            _state.update { it.copy(isLoading = false, errorMessage = e.message) }
        }
    }

    private fun MovieListItem.toDomainMovie(): Movie {
        return Movie(
            id = this.id.toString(),
            title = this.title,
            genre = "",
            posterUrl = this.posterUrl,
            age = this.ageRating,
            duration = "${this.duration} phút",
            format = "2D"
        )
    }
}
