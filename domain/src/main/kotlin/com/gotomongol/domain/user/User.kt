package com.gotomongol.domain.user

data class User(
    val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val role: UserRole = UserRole.USER,
    val termsAgreed: Boolean = false,
    val privacyAgreed: Boolean = false,
    val marketingAgreed: Boolean = false
)

enum class UserRole {
    USER, ADMIN
}
