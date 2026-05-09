package com.example.cah_cinema.presentation.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.cah_cinema.domain.model.Concession
import com.example.cah_cinema.domain.model.ConcessionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ConcessionState(
    val concessions: List<Concession> = emptyList(),
    val selectedSeatsDisplay: String = "",
    val seatTotalAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false
)

class ConcessionViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(ConcessionState())
    val state: StateFlow<ConcessionState> = _state.asStateFlow()

    private val seats: String = savedStateHandle["seats"] ?: ""
    private val totalAmountParam: Float = savedStateHandle["totalAmount"] ?: 0f
    private val dateArg: String = savedStateHandle["date"] ?: "06/04"
    private val timeArg: String = savedStateHandle["time"] ?: "18:20"

    init {
        _state.update { it.copy(
            selectedSeatsDisplay = seats,
            seatTotalAmount = totalAmountParam.toDouble(),
            totalAmount = totalAmountParam.toDouble()
        ) }
        loadConcessions()
    }

    private fun loadConcessions() {
        val mockData = listOf(
            Concession("1", "Pepsi lớn", 37000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/ly-pepsi-105151-111023-42.png", ConcessionType.DRINK),
            Concession("2", "Fanta lớn", 37000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/ly-fanta-105234-111023-56.png", ConcessionType.DRINK),
            Concession("3", "7Up lớn", 37000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/ly-7up-105214-111023-48.png", ConcessionType.DRINK),
            Concession("4", "Bắp thường", 53000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/bap-ngot-105253-111023-14.png", ConcessionType.POPCORN),
            Concession("5", "Bắp caramel", 57000.0, "https://files.betacinemas.vn/files/media/images/2023/10/11/bap-phomai-105314-111023-95.png", ConcessionType.POPCORN)
        )
        _state.update { it.copy(concessions = mockData) }
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
