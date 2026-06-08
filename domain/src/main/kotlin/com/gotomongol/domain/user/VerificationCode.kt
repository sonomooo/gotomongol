package com.gotomongol.domain.user

import java.time.LocalDateTime

data class VerificationCode(
    val id: Long = 0,
    val target: String,
    val type: VerificationType = VerificationType.LOGIN,
    val code: String,
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    val verified: Boolean = false
) {
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }

    fun isValid(inputCode: String): Boolean {
        return !isExpired() && !verified && code == inputCode
    }
}

enum class VerificationType {
    SIGNUP, LOGIN, RESERVATION
}
