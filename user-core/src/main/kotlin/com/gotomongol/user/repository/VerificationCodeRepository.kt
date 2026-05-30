package com.gotomongol.user.repository

import com.gotomongol.user.domain.VerificationCode
import org.springframework.data.jpa.repository.JpaRepository

interface VerificationCodeRepository : JpaRepository<VerificationCode, Long> {
    fun findTopByPhoneAndVerifiedFalseOrderByCreatedAtDesc(phone: String): VerificationCode?
}
