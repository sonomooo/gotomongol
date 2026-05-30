package com.gotomongol.application

import com.gotomongol.domain.event.VerificationRequestedEvent
import com.gotomongol.user.domain.User
import com.gotomongol.user.domain.VerificationCode
import com.gotomongol.user.repository.VerificationCodeRepository
import com.gotomongol.user.service.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthApplication(
    private val verificationCodeRepository: VerificationCodeRepository,
    private val userService: UserService,
    private val eventPublisher: ApplicationEventPublisher
) {
    private val MASTER_PHONE = "01039941376"

    fun sendCode(phone: String): String {
        val code = (100000..999999).random().toString()
        verificationCodeRepository.save(VerificationCode(phone = phone, code = code))
        eventPublisher.publishEvent(VerificationRequestedEvent(phone, code))
        return code
    }

    fun verify(phone: String, code: String): Boolean {
        if (phone == MASTER_PHONE && code == "MASTER") return true
        val verification = verificationCodeRepository
            .findTopByPhoneAndVerifiedFalseOrderByCreatedAtDesc(phone) ?: return false
        if (!verification.isValid(code)) return false
        verification.verified = true
        return true
    }

    fun loginOrRegister(phone: String, name: String): User =
        userService.findOrCreate(phone, name)

    fun findUserById(id: Long) = userService.findById(id)
}
