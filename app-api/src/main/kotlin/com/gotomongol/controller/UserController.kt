package com.gotomongol.controller

import com.gotomongol.user.dto.UserResponse
import com.gotomongol.user.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserResponse {
        val user = userService.findById(id)
        return UserResponse(user.id, user.name, user.phone, user.email, user.role.name)
    }

    @PatchMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody body: Map<String, String?>): UserResponse {
        val user = userService.update(id, body["name"], body["email"])
        return UserResponse(user.id, user.name, user.phone, user.email, user.role.name)
    }
}
