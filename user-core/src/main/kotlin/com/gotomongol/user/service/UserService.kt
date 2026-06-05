package com.gotomongol.user.service

import com.gotomongol.domain.port.UserPort
import com.gotomongol.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userPort: UserPort) {

    @Transactional
    fun findOrCreate(phone: String, name: String, termsAgreed: Boolean = true, privacyAgreed: Boolean = true, marketingAgreed: Boolean = false): User {
        return userPort.findByPhone(phone) ?: userPort.save(
            User(name = name, phone = phone, termsAgreed = termsAgreed, privacyAgreed = privacyAgreed, marketingAgreed = marketingAgreed)
        )
    }

    fun findByPhone(phone: String): User? {
        return userPort.findByPhone(phone)
    }

    fun findById(id: Long): User {
        return userPort.findById(id) ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
    }
}
