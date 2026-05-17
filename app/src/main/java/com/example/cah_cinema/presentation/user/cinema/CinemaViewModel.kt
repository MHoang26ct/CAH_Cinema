package com.example.cah_cinema.presentation.user.cinema

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.data.model.CinemaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CinemaState(
    val cinemas: List<Cinema> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class CinemaViewModel : ViewModel() {
    private val allCinemas = mutableListOf<Cinema>()
    private val _state = MutableStateFlow(CinemaState())
    val state: StateFlow<CinemaState> = _state.asStateFlow()

    init {
        loadCinemas()
    }

    private fun loadCinemas() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getCinemas()
                if (response.isSuccessful) {
                    val cinemaItems = response.body()?.data ?: emptyList()
                    val domainCinemas = cinemaItems.map { it.toDomainCinema() }
                    allCinemas.clear()
                    allCinemas.addAll(domainCinemas)
                    _state.update { it.copy(cinemas = domainCinemas, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải danh sách rạp") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun CinemaItem.toDomainCinema(): Cinema {
        return Cinema(
            id = this.id.toString(),
            name = this.name,
            address = this.address,
            phone = this.hotline
        )
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
