package com.gotomongol.domain.user

data class User(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String? = null,
    val role: UserRole = UserRole.USER
)

enum class UserRole {
    USER, ADMIN
}
