package com.gotomongol.domain.user

import java.time.LocalDateTime

data class VerificationCode(
    val id: Long = 0,
    val phone: String,
    val code: String,
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(3),
    val verified: Boolean = false
) {
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }

    fun isValid(inputCode: String): Boolean {
        return !isExpired() && !verified && code == inputCode
    }
}
