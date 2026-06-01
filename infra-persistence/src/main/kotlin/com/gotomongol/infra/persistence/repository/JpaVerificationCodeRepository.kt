package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.VerificationCodeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaVerificationCodeRepository : JpaRepository<VerificationCodeEntity, Long> {
    fun findTopByPhoneAndVerifiedFalseOrderByCreatedAtDesc(phone: String): VerificationCodeEntity?
}
