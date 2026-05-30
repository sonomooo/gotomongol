package com.gotomongol.user.domain

import com.gotomongol.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "verification_codes")
class VerificationCode(
    val phone: String,
    val code: String,
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(3),
    var verified: Boolean = false
) : BaseEntity() {

    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }

    fun isValid(inputCode: String): Boolean {
        return !isExpired() && !verified && code == inputCode
    }
}
