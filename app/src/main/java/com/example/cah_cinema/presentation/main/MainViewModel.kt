package com.example.cah_cinema.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cah_cinema.data.remote.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _startDestination = MutableStateFlow("login")
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            // Simulate initialization (e.g., checking token, fetching config)
            // In a real app, we would check SharedPreferences or EncryptedSharedPreferences here
            delay(1000) 
            
            // Check if user is already logged in (Mock logic)
            // If token existed, we could go to "home"
            
            _isReady.value = true
        }
    }
}
