package com.example.dailynotifications.backend.service

import com.example.dailynotifications.backend.dto.mobile.MobileLoginRequest
import com.example.dailynotifications.backend.dto.mobile.MobileRegisterRequest
import com.example.dailynotifications.backend.dto.response.UserAuthResponse
import com.example.dailynotifications.backend.dto.response.UserResponse
import com.example.dailynotifications.backend.entity.UserEntity
import com.example.dailynotifications.backend.exception.ConflictException
import com.example.dailynotifications.backend.exception.InvalidPasswordException
import com.example.dailynotifications.backend.repository.UserRepository
import com.example.dailynotifications.backend.security.AuthContext
import com.example.dailynotifications.backend.security.JwtService
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val authContext: AuthContext,
) {
    fun currentAuthenticatedEmail(): String = authContext.currentUserEmail()

    fun login(request: MobileLoginRequest): UserAuthResponse {
        val user = getEntityByEmail(request.email.trim().lowercase())
        if (user.password != request.password) {
            throw InvalidPasswordException("Invalid password")
        }
        val userId = requireNotNull(user.id)
        return UserAuthResponse(
            token = jwtService.generateToken(user.email, userId),
            user = user.toResponse(),
        )
    }

    @Transactional
    fun register(request: MobileRegisterRequest): UserAuthResponse {
        val email = request.email.trim().lowercase()
        if (userRepository.existsByEmail(email)) {
            throw ConflictException("User with email $email already exists")
        }

        val user = userRepository.save(
            UserEntity(
                name = request.name.trim(),
                email = email,
                password = request.password,
            ),
        )
        val userId = requireNotNull(user.id)
        return UserAuthResponse(
            token = jwtService.generateToken(user.email, userId),
            user = user.toResponse(),
        )
    }

    fun getCurrentUser(): UserResponse =
        getEntityByEmail(currentAuthenticatedEmail()).toResponse()

    fun getEntityByEmail(email: String): UserEntity =
        userRepository.findByEmail(email)
            ?: throw EntityNotFoundException("User not found")

    fun getEntityByEmailOrTokenId(email: String, userId: Long?): UserEntity =
        userRepository.findByEmail(email)
            ?: userId?.let { userRepository.findById(it).orElseThrow { EntityNotFoundException("User not found") } }
            ?: throw EntityNotFoundException("User not found")

    private fun UserEntity.toResponse(): UserResponse =
        UserResponse(
            id = requireNotNull(id),
            name = name,
            email = email,
        )
}
