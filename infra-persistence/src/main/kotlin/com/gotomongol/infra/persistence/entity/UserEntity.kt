package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    var name: String,

    @Column(unique = true)
    val phone: String? = null,

    @Column(unique = true)
    var email: String? = null,

    @Enumerated(EnumType.STRING)
    var role: UserRole = UserRole.USER,

    var termsAgreed: Boolean = false,
    var privacyAgreed: Boolean = false,
    var marketingAgreed: Boolean = false
) : BaseEntity()

enum class UserRole {
    USER, ADMIN
}
