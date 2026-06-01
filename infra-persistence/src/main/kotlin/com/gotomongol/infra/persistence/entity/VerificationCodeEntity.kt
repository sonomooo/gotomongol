package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "verification_codes")
class VerificationCodeEntity(
    val phone: String,
    val code: String,
    val expiresAt: LocalDateTime,
    var verified: Boolean = false
) : BaseEntity()
