package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.VerificationCodePort
import com.gotomongol.domain.user.VerificationCode
import com.gotomongol.infra.persistence.entity.VerificationCodeEntity
import com.gotomongol.infra.persistence.repository.JpaVerificationCodeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class VerificationCodeAdapter(private val repo: JpaVerificationCodeRepository) : VerificationCodePort {

    override fun save(code: VerificationCode): VerificationCode = repo.save(code.toEntity()).toDomain()

    override fun findLatestByPhone(phone: String): VerificationCode? =
        repo.findTopByPhoneAndVerifiedFalseOrderByCreatedAtDesc(phone)?.toDomain()

    override fun markVerified(id: Long) {
        repo.findByIdOrNull(id)?.let { it.verified = true; repo.save(it) }
    }

    private fun VerificationCodeEntity.toDomain() = VerificationCode(
        id = id, phone = phone, code = code, expiresAt = expiresAt, verified = verified
    )

    private fun VerificationCode.toEntity() = VerificationCodeEntity(
        phone = phone, code = code, expiresAt = expiresAt, verified = verified
    )
}
