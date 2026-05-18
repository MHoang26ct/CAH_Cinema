package com.example.cah_cinema.presentation.admin.food

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.model.FoodItem
import com.example.cah_cinema.data.repository.AdminRepositoryImpl
import com.example.cah_cinema.domain.repository.AdminRepository
import com.example.cah_cinema.util.CloudinaryUploader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminFoodState(
    val foods: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
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
                val resp = repository.getFoods()
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(foods = resp.data ?: emptyList(), isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi tải danh sách món ăn") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun uploadFoodImage(context: Context, imageUri: Uri, onResult: (String?) -> Unit) {
        _state.update { it.copy(isUploading = true) }
        viewModelScope.launch {
            val result = CloudinaryUploader.uploadImage(context, imageUri)
            _state.update { it.copy(isUploading = false) }
            result.fold(
                onSuccess = { url -> onResult(url) },
                onFailure = {
                    _state.update { s -> s.copy(errorMessage = "Upload ảnh thất bại: ${it.message}") }
                    onResult(null)
                }
            )
        }
    }

    fun createFood(food: FoodItem, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.createFood(food)
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(successMessage = "Thêm món thành công") }
                    loadFoods()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi thêm món") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun updateFood(id: Long, food: FoodItem, onSuccess: () -> Unit) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val resp = repository.updateFood(id, food)
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(successMessage = "Cập nhật thành công") }
                    loadFoods()
                    onSuccess()
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = resp?.message ?: "Lỗi cập nhật món") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun deleteFood(id: Long) {
        viewModelScope.launch {
            try {
                val resp = repository.deleteFood(id)
                if (resp != null && resp.code in 200..299) {
                    _state.update { it.copy(successMessage = "Xóa món thành công") }
                    loadFoods()
                } else {
                    _state.update { it.copy(errorMessage = resp?.message ?: "Lỗi xóa món") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "Lỗi kết nối: ${e.message}") }
            }
        }
    }

    fun clearMessages() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
