package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    var name: String,

    @Column(unique = true)
    val phone: String,

    var email: String? = null,

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER
) : BaseEntity()

enum class UserRole {
    USER, ADMIN
}
