package com.example.cah_cinema.presentation.user.cinema

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.repository.CinemaRepositoryImpl
import com.example.cah_cinema.domain.model.Cinema
import com.example.cah_cinema.domain.repository.CinemaRepository
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

class CinemaViewModel(
    private val repository: CinemaRepository = CinemaRepositoryImpl()
) : ViewModel() {
    private val allCinemas = mutableListOf<Cinema>()
    private val _state = MutableStateFlow(CinemaState())
    val state: StateFlow<CinemaState> = _state.asStateFlow()

    init {
        loadCinemas()
    }

    private fun loadCinemas() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            repository.getCinemas().onSuccess { domainCinemas ->
                allCinemas.clear()
                allCinemas.addAll(domainCinemas)
                _state.update { it.copy(cinemas = domainCinemas, isLoading = false) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, errorMessage = error.message ?: "Không thể tải danh sách rạp") }
            }
        }
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
