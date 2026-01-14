package com.example.dailynotifications.data.repository.auth

import android.content.SharedPreferences
import com.example.dailynotifications.data.repository.auth.AuthRepository
import com.example.dailynotifications.data.repository.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class SharedPrefsAuthRepository(
    private val prefs: SharedPreferences
) : AuthRepository {

    private val _authState = MutableStateFlow(readState())
    override val authState: StateFlow<AuthState> = _authState

    override suspend fun login(email: String, password: String) {
        val token = UUID.randomUUID().toString()
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, "User")
            .putBoolean(KEY_GUEST, false)
            .apply()
        _authState.value = readState()
    }

    override suspend fun register(name: String, email: String, password: String) {
        val token = UUID.randomUUID().toString()
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .putBoolean(KEY_GUEST, false)
            .apply()
        _authState.value = readState()
    }

    override fun logout() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_EMAIL)
            .remove(KEY_NAME)
            .putBoolean(KEY_GUEST, true)
            .apply()
        _authState.value = readState()
    }

    override fun skipAuth() {
        prefs.edit().putBoolean(KEY_GUEST, true).apply()
        _authState.value = readState()
    }

    override fun startAuth() {
        prefs.edit().putBoolean(KEY_GUEST, false).apply()
        _authState.value = readState()
    }

    private fun readState(): AuthState {
        val token = prefs.getString(KEY_TOKEN, null)
        val email = prefs.getString(KEY_EMAIL, null)
        val name = prefs.getString(KEY_NAME, null)
        val isGuest = prefs.getBoolean(KEY_GUEST, false)
        val isLoggedIn = !token.isNullOrBlank()
        return AuthState(
            isLoggedIn = isLoggedIn,
            isGuest = if (isLoggedIn) false else isGuest,
            token = token,
            name = name,
            email = email
        )
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_EMAIL = "email"
        const val KEY_NAME = "name"
        const val KEY_GUEST = "guest"
    }
}
