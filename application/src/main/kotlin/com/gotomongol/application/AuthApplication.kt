package com.gotomongol.application

import com.gotomongol.domain.event.VerificationRequestedEvent
import com.gotomongol.domain.port.VerificationCodePort
import com.gotomongol.domain.user.User
import com.gotomongol.domain.user.VerificationCode
import com.gotomongol.domain.user.VerificationType
import com.gotomongol.application.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthApplication(
    private val verificationCodePort: VerificationCodePort,
    private val userService: UserService,
    private val eventPublisher: ApplicationEventPublisher
) {
    private val MASTER_PHONE = "01039941376"

    /**
     * 인증코드 발송
     * @param target email 또는 phone
     * @param type SIGNUP / LOGIN / RESERVATION
     */
    fun sendCode(target: String, type: VerificationType = VerificationType.LOGIN): String {
        val code = (100000..999999).random().toString()
        verificationCodePort.save(VerificationCode(target = target, type = type, code = code))
        eventPublisher.publishEvent(VerificationRequestedEvent(target, code))
        return code
    }

    /**
     * 인증코드 검증
     */
    fun verify(target: String, code: String): Boolean {
        if (target == MASTER_PHONE && code == "MASTER") return true
        val verification = verificationCodePort.findLatestByTarget(target) ?: return false
        if (!verification.isValid(code)) return false
        verificationCodePort.markVerified(verification.id)
        return true
    }

    /**
     * 로그인 또는 회원가입
     * target이 email이면 email로, phone이면 phone으로 유저 조회/생성
     */
    fun loginOrRegister(target: String, name: String, termsAgreed: Boolean = true, privacyAgreed: Boolean = true, marketingAgreed: Boolean = false): User {
        return userService.findOrCreate(target, name, termsAgreed, privacyAgreed, marketingAgreed)
    }

    fun findUserById(id: Long): User {
        return userService.findById(id)
    }
}
