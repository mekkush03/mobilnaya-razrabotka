package com.example.dailynotifications.backend.exception

import com.example.dailynotifications.backend.dto.response.ApiErrorResponse
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiErrorResponse(ex.message ?: "Conflict"))

    @ExceptionHandler(InvalidPasswordException::class)
    fun handleInvalidPassword(ex: InvalidPasswordException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiErrorResponse(ex.message ?: "Invalid credentials"))

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiErrorResponse(ex.message ?: "Entity not found"))

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiErrorResponse(ex.message ?: "Access denied"))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse(ex.message ?: "Bad request"))

    @ExceptionHandler(MethodArgumentNotValidException::class, HandlerMethodValidationException::class)
    fun handleValidation(ex: Exception): ResponseEntity<ApiErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiErrorResponse(ex.message ?: "Validation failed"))
}
