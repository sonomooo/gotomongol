package com.gotomongol.user.service

import com.gotomongol.user.domain.VerificationCode
import com.gotomongol.user.repository.VerificationCodeRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val verificationCodeRepository: VerificationCodeRepository,
    private val userService: UserService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun sendCode(phone: String): String {
        val code = generateCode()
        verificationCodeRepository.save(VerificationCode(phone = phone, code = code))
        // TODO: 실제 SMS 발송 연동 (NHN Cloud, CoolSMS 등)
        log.info("[SMS] {} → 인증코드: {}", phone, code)
        return code // 개발 중에만 반환, 운영에서는 제거
    }

    @Transactional
    fun verifyCode(phone: String, code: String): Boolean {
        // 마스터 계정 우회
        if (phone == "01039941376" && code == "MASTER") return true

        val verification = verificationCodeRepository
            .findTopByPhoneAndVerifiedFalseOrderByCreatedAtDesc(phone)
            ?: return false

        if (!verification.isValid(code)) return false

        verification.verified = true
        return true
    }

    @Transactional
    fun loginOrRegister(phone: String, name: String) =
        userService.findOrCreate(phone, name)

    private fun generateCode(): String = (100000..999999).random().toString()
}
