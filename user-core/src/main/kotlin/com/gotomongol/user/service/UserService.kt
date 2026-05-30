package com.gotomongol.user.service

import com.gotomongol.user.domain.User
import com.gotomongol.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun findOrCreate(phone: String, name: String): User {
        return userRepository.findByPhone(phone) ?: userRepository.save(User(name = name, phone = phone))
    }

    fun findByPhone(phone: String): User? {
        return userRepository.findByPhone(phone)
    }

    fun findById(id: Long): User {
        return userRepository.findById(id).orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
    }

    @Transactional
    fun update(id: Long, name: String?, email: String?): User {
        val user = findById(id)
        name?.let { user.name = it }
        email?.let { user.email = it }
        return user
    }
}
