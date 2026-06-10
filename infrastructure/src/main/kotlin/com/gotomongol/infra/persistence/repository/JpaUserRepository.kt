package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<UserEntity, Long> {
    fun findByPhone(phone: String): UserEntity?
    fun findByEmail(email: String): UserEntity?
    fun existsByPhone(phone: String): Boolean
    fun existsByEmail(email: String): Boolean
}
