package com.example.dailynotifications.data.repository.auth

import android.content.SharedPreferences
import com.example.dailynotifications.data.remote.AuthApi
import com.example.dailynotifications.data.remote.dto.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedPrefsAuthRepository(
    private val prefs: SharedPreferences,
    private val authApi: AuthApi
) : AuthRepository {

    private val _authState = MutableStateFlow(readState())
    override val authState: StateFlow<AuthState> = _authState

    override suspend fun login(email: String, password: String) {
        val session = authApi.login(email, password)
        saveSession(session)
    }

    override suspend fun register(name: String, email: String, password: String) {
        val session = authApi.register(name, email, password)
        saveSession(session)
    }

    override fun logout() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_EMAIL)
            .remove(KEY_NAME)
            .putBoolean(KEY_GUEST, false)
            .apply()
        _authState.value = readState()
    }

    override fun skipAuth() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_EMAIL)
            .remove(KEY_NAME)
            .putBoolean(KEY_GUEST, true)
            .apply()
        _authState.value = readState()
    }

    override fun startAuth() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_EMAIL)
            .remove(KEY_NAME)
            .putBoolean(KEY_GUEST, false)
            .apply()
        _authState.value = readState()
    }

    private fun saveSession(session: AuthSession) {
        prefs.edit()
            .putString(KEY_TOKEN, session.token)
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_NAME, session.name)
            .putBoolean(KEY_GUEST, false)
            .apply()
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
