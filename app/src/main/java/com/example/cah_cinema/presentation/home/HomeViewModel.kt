package com.example.cah_cinema.presentation.home

import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeState(
    val userName: String = "Hinno",
    val featuredMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList()
)

class HomeViewModel : ViewModel() {
    private val dummyFeatured = listOf(
        Movie(
            id = "hen_em_ngay_nhat_thuc",
            title = "Hẹn Em Ngày Nhật Thực",
            genre = "Tình cảm",
            posterUrl = "https://scontent.fsgn1-1.fna.fbcdn.net/v/t39.30808-6/666425663_1729312211678042_5496145058148432723_n.jpg?stp=dst-jpg_p843x403_tt6&_nc_cat=108&ccb=1-7&_nc_sid=13d280&_nc_eui2=AeGE_VnXbp5wp2v74NrdWFOccKxwcXYPrMpwrHBxdg-syjmZPhmrDE90E2ySAgWlB1LRQToHRg13L9hyZ-dKiA3e&_nc_ohc=74e0CwUsfzAQ7kNvwH2MIFo&_nc_oc=AdpGlO3WRk37wQyfs-YdINiVo2Z9SfzeOiMyb48H0Vp3LKNGq04mkmgagmvnlt04Evk&_nc_zt=23&_nc_ht=scontent.fsgn1-1.fna&_nc_gid=bdilNMLB59j7tqKHLws-Aw&_nc_ss=7b2a8&oh=00_Af6BGtqd9yYSzORrXowi6M_LhVVl0mumBfst_kbeP0gTlw&oe=6A01BAAE",
            bannerUrl = "https://files.betacinemas.vn/files/media/images/2024/04/16/434863920-1123447998937086-458145417830209700-n-102551-160424-42.jpg",
            format = "2D",
            age = "T16",
            duration = "118 phút"
        ),
        Movie(
            id = "4",
            title = "Kung Fu Panda 4",
            genre = "Hoạt hình/Hành động",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BZjE0ZjExZTYtN2U0OC00MDlhLWJjNmEtN2ZjN2U1NWZlZGRlXkEyXkFqcGc@._V1_.jpg",
            format = "3D",
            age = "P"
        )
    )

    private val dummyUpcoming = listOf(
        Movie(
            id = "2",
            title = "Captain America 4",
            genre = "Hành động/Viễn tưởng",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMjA5OTFlY2EtYzA3My00Y2VjLTg0ZDMtY2I2Y2I3Y2JmYzY5XkEyXkFqcGc@._V1_.jpg",
            releaseDate = "14/02/2025"
        ),
        Movie(
            id = "3",
            title = "Superman",
            genre = "Hành động/Viễn tưởng",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BYjFlM2E2YTUtYWU2MC00N2EzLTkyYmEtNjYxMTE0Y2M1OTU2XkEyXkFqcGc@._V1_.jpg",
            releaseDate = "11/07/2025"
        )
    )

    private val _state = MutableStateFlow(
        HomeState(
            userName = "Hinno",
            featuredMovies = dummyFeatured,
            upcomingMovies = dummyUpcoming
        )
    )
    val state: StateFlow<HomeState> = _state.asStateFlow()
}
