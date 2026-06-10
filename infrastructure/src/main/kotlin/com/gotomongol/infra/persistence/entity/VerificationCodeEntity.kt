package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "verification_codes")
class VerificationCodeEntity(
    val target: String = "",
    val type: String = "LOGIN",
    val code: String = "",
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(5),
    var verified: Boolean = false
) : BaseEntity()
