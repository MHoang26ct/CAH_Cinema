package com.example.cah_cinema.presentation.admin.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.FoodItem
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminFoodState(
    val foods: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AdminFoodViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(AdminFoodState())
    val state: StateFlow<AdminFoodState> = _state.asStateFlow()

    init {
        loadFoods()
    }

    fun loadFoods() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = repository.getAllFood()
                if (response?.code == 200) {
                    _state.update { it.copy(foods = response.data ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi tải danh sách đồ ăn") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun createFood(food: FoodItem, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = repository.createFood(food)
                if (response?.code == 200) {
                    loadFoods()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi thêm đồ ăn") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun updateFood(id: Long, food: FoodItem, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val response = repository.updateFood(id, food)
                if (response?.code == 200) {
                    loadFoods()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = response?.message ?: "Lỗi cập nhật đồ ăn") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteFood(id: Long) {
        viewModelScope.launch {
            try {
                val response = repository.deleteFood(id)
                if (response?.code == 200) {
                    loadFoods()
                }
            } catch (e: Exception) {}
        }
    }
}
