package com.example.dailynotifications.backend.security

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${security.jwt.secret}") secret: String,
    @Value("\${security.jwt.expiration-hours}") private val expirationHours: Long,
) {
    private val signingKey: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())
    private val parser: JwtParser = Jwts.parser().verifyWith(signingKey).build()

    fun generateToken(email: String, userId: Long): String {
        val now = Date()
        val expiresAt = Date(now.time + expirationHours * 60 * 60 * 1000)
        return Jwts.builder()
            .subject(email)
            .claim("userId", userId)
            .claim("roles", listOf("ROLE_USER"))
            .issuedAt(now)
            .expiration(expiresAt)
            .signWith(signingKey)
            .compact()
    }

    fun extractEmail(token: String): String =
        parser.parseSignedClaims(token).payload.subject

    fun extractUserId(token: String): Long? =
        parser.parseSignedClaims(token).payload.get("userId", Number::class.java)?.toLong()

    fun isValid(token: String): Boolean =
        runCatching { parser.parseSignedClaims(token) }.isSuccess
}
