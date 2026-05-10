package com.example.cah_cinema.presentation.cinema

import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.util.ImageUrls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CinemaState(
    val cinemas: List<Cinema> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class CinemaViewModel : ViewModel() {
    private val allCinemas = mutableListOf<Cinema>()
    private val _state = MutableStateFlow(CinemaState())
    val state: StateFlow<CinemaState> = _state.asStateFlow()

    init {
        loadCinemas()
    }

    private fun loadCinemas() {
        val mockCinemas = listOf(
            Cinema(
                id = "1",
                name = "Cinestar Sinh viên (Bình Dương)",
                address = "Nhà văn hóa Sinh viên, Đại học Quốc Gia HCM, Phường Đông Hòa, Thành phố Hồ Chí Minh",
                imageUrl = ImageUrls.CINESTAR_QUOC_THANH,
                phone = "1900.0085"
            ),
            Cinema(
                id = "2",
                name = "Cinestar Huế(TP.Huế)",
                address = "25 Hai Bà Trưng, Phường Thuận Hòa, Thành phố Huế",
                imageUrl = ImageUrls.CINESTAR_HUE,
                phone = "1900.0085"
            ),
            Cinema(
                id = "3",
                name = "Cinestar Đà Lạt(TP.Đà Lạt)",
                address = "Quảng trường Lâm Viên, góc đường Hồ Tùng Mậu và Trần Quốc Toản, Phường Xuân Hương - Đà Lạt, Tỉnh Lâm Đồng",
                imageUrl = ImageUrls.CINESTAR_DA_LAT,
                phone = "1900.0085"
            ),
            Cinema(
                id = "4",
                name = "Cinestar Quốc Thanh",
                address = "271 Nguyễn Trãi, Phường Nguyễn Cư Trinh, Quận 1, Thành phố Hồ Chí Minh",
                imageUrl = ImageUrls.CINESTAR_QUOC_THANH,
                phone = "1900.0085"
            ),
            Cinema(
                id = "5",
                name = "Cinestar Mỹ Tho (Tiền Giang)",
                address = "Số 52 Đinh Bộ Lĩnh, Phường Mỹ Tho, Tỉnh Đồng Tháp",
                imageUrl = ImageUrls.CINESTAR_MY_THO,
                phone = "1900.0085"
            ),
            Cinema(
                id = "6",
                name = "Cinestar Lâm Đồng (Đức Trọng)",
                address = "Tầng 4, Trung tâm thương mại và Dịch vụ tài chính Sacombank, 713 Quốc lộ 20, Xã Đức Trọng, Tỉnh Lâm Đồng",
                imageUrl = ImageUrls.CINESTAR_LAM_DONG,
                phone = "1900.0085"
            )
        )
        allCinemas.clear()
        allCinemas.addAll(mockCinemas)
        _state.update { it.copy(cinemas = mockCinemas) }
    }

    fun onSearchQueryChange(newQuery: String) {
        _state.update { currentState ->
            val filteredCinemas = if (newQuery.isBlank()) {
                allCinemas
            } else {
                allCinemas.filter { 
                    it.name.contains(newQuery, ignoreCase = true) || 
                    it.address.contains(newQuery, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = newQuery,
                cinemas = filteredCinemas
            )
        }
    }
}
