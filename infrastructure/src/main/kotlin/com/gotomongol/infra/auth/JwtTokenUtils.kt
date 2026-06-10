package com.gotomongol.infra.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

object JwtTokenUtils {
    private val secret: String = System.getenv("JWT_SECRET") ?: error("JWT_SECRET 환경변수가 설정되지 않았습니다.")
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    private const val ACCESS_TOKEN_VALIDITY_MINUTES = 30L
    private const val REFRESH_TOKEN_VALIDITY_DAYS = 7L

    fun generateAccessToken(userId: Long, claims: Map<String, Any> = emptyMap()): String {
        return Jwts.builder()
            .claims(claims)
            .id(userId.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MINUTES * 60 * 1000))
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(userId: Long): String {
        return Jwts.builder()
            .id(userId.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_DAYS * 24 * 60 * 60 * 1000))
            .signWith(key)
            .compact()
    }

    fun getUserIdFromToken(token: String): Long {
        return getClaims(token).id.toLong()
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            getClaims(token).expiration.before(Date())
        } catch (e: ExpiredJwtException) {
            true
        }
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
