package com.example.cah_cinema.presentation.user.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.Promotion
import com.example.cah_cinema.data.model.MovieListItem
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val userName: String = "",
    val avatarUrl: String? = null,
    val featuredMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val promotions: List<Promotion> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        fetchFeaturedMovies()
        loadUserProfile()
        loadPromotions()
    }

    private fun loadPromotions() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getPublicPromotions(page = 0, size = 9)
                if (response.isSuccessful) {
                    val items = response.body()?.data?.content ?: emptyList()
                    val promotions = items.map { item ->
                        Promotion(
                            id = item.id.toString(),
                            title = item.title,
                            description = item.description ?: "",
                            imageUrl = item.imageUrl ?: ""
                        )
                    }
                    _state.update { it.copy(promotions = promotions) }
                } else {
                    Log.w("HomeViewModel", "getPublicPromotions failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading promotions", e)
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyProfile()
                if (response.isSuccessful) {
                    val user = response.body()?.data?.user
                    _state.update {
                        it.copy(
                            userName = user?.name ?: it.userName,
                            avatarUrl = user?.avatarUrl
                        )
                    }
                } else {
                    // fallback: đọc avatar từ cache local
                    val cachedAvatar = RetrofitClient.getLocalAvatarUrl()
                    if (!cachedAvatar.isNullOrBlank()) {
                        _state.update { it.copy(avatarUrl = cachedAvatar) }
                    }
                }
            } catch (_: Exception) {
                val cachedAvatar = RetrofitClient.getLocalAvatarUrl()
                if (!cachedAvatar.isNullOrBlank()) {
                    _state.update { it.copy(avatarUrl = cachedAvatar) }
                }
            }
        }
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
