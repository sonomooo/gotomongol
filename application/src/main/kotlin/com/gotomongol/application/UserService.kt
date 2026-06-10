package com.gotomongol.application

import com.gotomongol.domain.port.UserPort
import com.gotomongol.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userPort: UserPort) {

    @Transactional
    fun findOrCreate(target: String, name: String, termsAgreed: Boolean = true, privacyAgreed: Boolean = true, marketingAgreed: Boolean = false): User {
        val existing = findByTarget(target)
        if (existing != null) return existing

        val isEmail = target.contains("@")
        return userPort.save(User(
            name = name,
            phone = if (!isEmail) target else null,
            email = if (isEmail) target else null,
            termsAgreed = termsAgreed,
            privacyAgreed = privacyAgreed,
            marketingAgreed = marketingAgreed
        ))
    }

    fun findByTarget(target: String): User? {
        return if (target.contains("@")) userPort.findByEmail(target)
        else userPort.findByPhone(target)
    }

    fun findByPhone(phone: String): User? {
        return userPort.findByPhone(phone)
    }

    fun findById(id: Long): User {
        return userPort.findById(id) ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")
    }
}
