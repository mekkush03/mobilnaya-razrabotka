package com.example.dailynotifications.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailynotifications.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val authState = authRepository.authState
    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events

    fun onLogin(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _events.emit("Enter email and password.")
                return@launch
            }
            authRepository.login(email, password)
        }
    }

    fun onRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                _events.emit("Fill all fields.")
                return@launch
            }
            authRepository.register(name, email, password)
        }
    }

    fun onSkip() {
        authRepository.skipAuth()
    }
}
