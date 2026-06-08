package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.VerificationCodePort
import com.gotomongol.domain.user.VerificationCode
import com.gotomongol.domain.user.VerificationType
import com.gotomongol.infra.persistence.entity.VerificationCodeEntity
import com.gotomongol.infra.persistence.repository.JpaVerificationCodeRepository
import org.springframework.stereotype.Component

@Component
class VerificationCodeAdapter(private val repo: JpaVerificationCodeRepository) : VerificationCodePort {

    override fun save(code: VerificationCode): VerificationCode {
        val entity = repo.save(VerificationCodeEntity(
            target = code.target, type = code.type.name,
            code = code.code, expiresAt = code.expiresAt, verified = code.verified
        ))
        return toDomain(entity)
    }

    override fun findLatestByTarget(target: String): VerificationCode? {
        return repo.findTopByTargetAndVerifiedFalseOrderByIdDesc(target)?.let { toDomain(it) }
    }

    override fun markVerified(id: Long) {
        repo.findById(id).ifPresent { it.verified = true; repo.save(it) }
    }

    private fun toDomain(e: VerificationCodeEntity) = VerificationCode(
        id = e.id, target = e.target, type = VerificationType.valueOf(e.type),
        code = e.code, expiresAt = e.expiresAt, verified = e.verified
    )
}
