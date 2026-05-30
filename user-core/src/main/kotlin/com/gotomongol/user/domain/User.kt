package com.gotomongol.user.domain

import com.gotomongol.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    var name: String,

    @Column(unique = true, nullable = false)
    val phone: String,

    var email: String? = null,

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER
) : BaseEntity()

enum class UserRole {
    USER, ADMIN
}
