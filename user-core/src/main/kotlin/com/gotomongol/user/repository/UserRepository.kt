package com.gotomongol.user.repository

import com.gotomongol.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByPhone(phone: String): User?
    fun existsByPhone(phone: String): Boolean
}
