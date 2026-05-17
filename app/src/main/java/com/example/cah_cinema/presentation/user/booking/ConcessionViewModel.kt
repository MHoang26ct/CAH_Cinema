package com.example.cah_cinema.presentation.user.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.domain.model.Concession
import com.example.cah_cinema.domain.model.ConcessionType
import com.example.cah_cinema.data.remote.RetrofitClient
import com.example.cah_cinema.data.model.FoodItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConcessionState(
    val concessions: List<Concession> = emptyList(),
    val selectedSeatsDisplay: String = "",
    val seatTotalAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ConcessionViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(ConcessionState())
    val state: StateFlow<ConcessionState> = _state.asStateFlow()

    private val seats: String = savedStateHandle["seats"] ?: ""
    private val totalAmountParam: Float = savedStateHandle["totalAmount"] ?: 0f

    init {
        _state.update { it.copy(
            selectedSeatsDisplay = seats,
            seatTotalAmount = totalAmountParam.toDouble(),
            totalAmount = totalAmountParam.toDouble()
        ) }
        loadConcessions()
    }

    private fun loadConcessions() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getFoods()
                if (response.isSuccessful) {
                    val foodItems = response.body()?.data ?: emptyList()
                    val domainConcessions = foodItems.map { it.toDomainConcession() }
                    _state.update { it.copy(concessions = domainConcessions, isLoading = false) }
                } else {
                    _state.update { it.copy(isLoading = false, errorMessage = "Không thể tải danh sách bắp nước") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun FoodItem.toDomainConcession(): Concession {
        return Concession(
            id = this.id.toString(),
            name = this.name,
            price = this.price,
            imageUrl = this.imageUrl,
            type = when(this.category) {
                "Drink" -> ConcessionType.DRINK
                "Food" -> ConcessionType.POPCORN
                else -> ConcessionType.COMBO
            }
        )
    }

    fun onQuantityChange(concessionId: String, delta: Int) {
        _state.update { currentState ->
            val updatedList = currentState.concessions.map {
                if (it.id == concessionId) {
                    val newQuantity = (it.quantity + delta).coerceAtLeast(0)
                    it.copy(quantity = newQuantity)
                } else {
                    it
                }
            }
            val concessionTotal = updatedList.sumOf { it.price * it.quantity }
            currentState.copy(
                concessions = updatedList,
                totalAmount = currentState.seatTotalAmount + concessionTotal
            )
        }
    }
}
