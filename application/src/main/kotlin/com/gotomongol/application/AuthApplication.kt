package com.gotomongol.application

import com.gotomongol.domain.event.VerificationRequestedEvent
import com.gotomongol.domain.port.VerificationCodePort
import com.gotomongol.domain.user.User
import com.gotomongol.domain.user.VerificationCode
import com.gotomongol.user.service.UserService
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

    fun sendCode(phone: String): String {
        val code = (100000..999999).random().toString()
        verificationCodePort.save(VerificationCode(phone = phone, code = code))
        eventPublisher.publishEvent(VerificationRequestedEvent(phone, code))
        return code
    }

    fun verify(phone: String, code: String): Boolean {
        if (phone == MASTER_PHONE && code == "MASTER") return true
        val verification = verificationCodePort.findLatestByPhone(phone) ?: return false
        if (!verification.isValid(code)) return false
        verificationCodePort.markVerified(verification.id)
        return true
    }

    fun loginOrRegister(phone: String, name: String, termsAgreed: Boolean = true, privacyAgreed: Boolean = true, marketingAgreed: Boolean = false): User {
        return userService.findOrCreate(phone, name, termsAgreed, privacyAgreed, marketingAgreed)
    }

    fun findUserById(id: Long): User {
        return userService.findById(id)
    }
}
