package com.gotomongol.user.dto

data class UserResponse(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String?,
    val role: String
)
