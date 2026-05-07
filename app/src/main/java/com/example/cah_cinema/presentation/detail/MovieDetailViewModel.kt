package com.example.cah_cinema.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.domain.model.Movie
import com.example.cah_cinema.domain.model.MovieDate
import com.example.cah_cinema.domain.model.Showtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MovieDetailState(
    val movie: Movie? = null,
    val availableDates: List<MovieDate> = emptyList(),
    val cinemas: List<Cinema> = emptyList(),
    val isLoading: Boolean = false
)

class MovieDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(MovieDetailState())
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    private val movieId: String? = savedStateHandle["movieId"]

    private val mockMovies = mapOf(
        "hen_em_ngay_nhat_thuc" to Movie(
            id = "hen_em_ngay_nhat_thuc",
            title = "HẸN EM NGÀY NHẬT THỰC (T16)",
            duration = "118 phút",
            genre = "Tình cảm",
            posterUrl = "https://scontent.fsgn1-1.fna.fbcdn.net/v/t39.30808-6/666425663_1729312211678042_5496145058148432723_n.jpg?stp=dst-jpg_p843x403_tt6&_nc_cat=108&ccb=1-7&_nc_sid=13d280&_nc_eui2=AeGE_VnXbp5wp2v74NrdWFOccKxwcXYPrMpwrHBxdg-syjmZPhmrDE90E2ySAgWlB1LRQToHRg13L9hyZ-dKiA3e&_nc_ohc=74e0CwUsfzAQ7kNvwH2MIFo&_nc_oc=AdpGlO3WRk37wQyfs-YdINiVo2Z9SfzeOiMyb48H0Vp3LKNGq04mkmgagmvnlt04Evk&_nc_zt=23&_nc_ht=scontent.fsgn1-1.fna&_nc_gid=bdilNMLB59j7tqKHLws-Aw&_nc_ss=7b2a8&oh=00_Af6BGtqd9yYSzORrXowi6M_LhVVl0mumBfst_kbeP0gTlw&oe=6A01BAAE",
            bannerUrl = "https://files.betacinemas.vn/files/media/images/2024/04/16/434863920-1123447998937086-458145417830209700-n-102551-160424-42.jpg",
            format = "2D",
            age = "T16",
            director = "Lê Thiện Viễn",
            cast = "Đoàn Thiên Ân, Khương Lê, NSND Lê Khanh, Huỳnh Phương, Nguyên Thảo",
            description = "Năm 1995, khi đang đứng trước một quyết định quan trọng của cuộc đời, Ân bất ngờ bị kéo trở lại quá khứ bởi những bức thư tình chưa từng trao tay. Hành trình tìm gặp Thiên ..."
        ),
        "4" to Movie(
            id = "4",
            title = "Kung Fu Panda 4",
            duration = "94 phút",
            genre = "Hoạt hình/Hành động",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BZjE0ZjExZTYtN2U0OC00MDlhLWJjNmEtN2ZjN2U1NWZlZGRlXkEyXkFqcGc@._V1_.jpg",
            bannerUrl = "https://m.media-amazon.com/images/M/MV5BNjU0NDU1YmUtN2NkOS00YjVjLWEwM2ItMWY5M2RkZGNlNjA4XkEyXkFqcGc@._V1_QL75_UX500_CR0,0,500,281_.jpg",
            format = "3D",
            age = "P",
            director = "Mike Mitchell",
            cast = "Jack Black, Awkwafina, Viola Davis",
            description = "Sau khi được chọn trở thành Thủ lĩnh tinh thần của Thung lũng Bình Yên, Po cần tìm và huấn luyện một Hiệp sĩ Rồng mới, trong khi một nữ phù thủy độc ác lên kế hoạch triệu hồi tất cả những kẻ phản diện mà Po đã đánh bại về cõi linh hồn."
        ),
        "2" to Movie(
            id = "2",
            title = "Captain America: Brave New World",
            duration = "120 phút",
            genre = "Hành động/Viễn tưởng",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BMjA5OTFlY2EtYzA3My00Y2VjLTg0ZDMtY2I2Y2I3Y2JmYzY5XkEyXkFqcGc@._V1_.jpg",
            bannerUrl = "https://m.media-amazon.com/images/M/MV5BMjA5OTFlY2EtYzA3My00Y2VjLTg0ZDMtY2I2Y2I3Y2JmYzY5XkEyXkFqcGc@._V1_QL75_UX500_CR0,0,500,281_.jpg",
            format = "2D",
            age = "T13",
            director = "Julius Onah",
            cast = "Anthony Mackie, Harrison Ford",
            description = "Sam Wilson chính thức tiếp bước Steve Rogers để trở thành Captain America mới, đối mặt với những âm mưu chính trị và kẻ thù mới đầy nguy hiểm."
        ),
        "3" to Movie(
            id = "3",
            title = "Superman",
            duration = "150 phút",
            genre = "Hành động/Viễn tưởng",
            posterUrl = "https://m.media-amazon.com/images/M/MV5BYjFlM2E2YTUtYWU2MC00N2EzLTkyYmEtNjYxMTE0Y2M1OTU2XkEyXkFqcGc@._V1_.jpg",
            bannerUrl = "https://m.media-amazon.com/images/M/MV5BYjFlM2E2YTUtYWU2MC00N2EzLTkyYmEtNjYxMTE0Y2M1OTU2XkEyXkFqcGc@._V1_QL75_UX500_CR0,0,500,281_.jpg",
            format = "2D",
            age = "P",
            director = "James Gunn",
            cast = "David Corenswet, Rachel Brosnahan",
            description = "Câu chuyện về Superman khi anh cố gắng cân bằng di sản Krypton của mình với cuộc sống làm người Trái Đất tại Smallville."
        )
    )

    init {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        val movie = mockMovies[movieId] ?: mockMovies["hen_em_ngay_nhat_thuc"]

        val mockDates = listOf(
            MovieDate("Hôm nay", "06/04", true),
            MovieDate("Thứ 3", "07/04"),
            MovieDate("Thứ 4", "08/04"),
            MovieDate("Thứ 5", "09/04"),
            MovieDate("Thứ 6", "10/04"),
            MovieDate("Thứ 7", "11/04")
        )

        val mockCinemas = listOf(
            Cinema(
                id = "c1",
                name = "Cinestar Quốc Thanh (TP.HCM)",
                address = "271 Nguyễn Trãi, Phường Nguyễn Cư Trinh, Quận 1, Thành phố Hồ Chí Minh",
                showtimes = listOf(
                    Showtime("s1", "18:20"),
                    Showtime("s2", "18:50"),
                    Showtime("s3", "20:30"),
                    Showtime("s4", "22:15")
                )
            ),
            Cinema(
                id = "c2",
                name = "Cinestar Hai Bà Trưng",
                address = "233 Hai Bà Trưng, Phường 6, Quận 3, TP.HCM",
                showtimes = listOf(
                    Showtime("s5", "19:00"),
                    Showtime("s6", "21:45")
                )
            )
        )

        _state.update {
            it.copy(
                movie = movie,
                availableDates = mockDates,
                cinemas = mockCinemas
            )
        }
    }

    fun onDateSelected(selectedDate: MovieDate) {
        _state.update { currentState ->
            currentState.copy(
                availableDates = currentState.availableDates.map {
                    it.copy(isSelected = it.date == selectedDate.date)
                }
            )
        }
    }
}
