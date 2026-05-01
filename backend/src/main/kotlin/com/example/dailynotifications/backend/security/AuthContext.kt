package com.example.dailynotifications.backend.security

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthContext {
    fun currentUserEmail(): String {
        val auth = SecurityContextHolder.getContext().authentication
        return auth?.name?.takeIf { auth.isAuthenticated } ?: throw AccessDeniedException("Unauthorized")
    }

    fun requireEmail(email: String) {
        if (currentUserEmail() != email) {
            throw AccessDeniedException("Access denied")
        }
    }
}
