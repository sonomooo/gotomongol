package com.gotomongol.controller

import com.gotomongol.user.dto.UserResponse
import com.gotomongol.user.service.AuthService
import com.gotomongol.user.service.UserService
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val userService: UserService
) {

    /** 1단계: 인증코드 발송 */
    @PostMapping("/send-code")
    fun sendCode(@RequestBody body: SendCodeRequest): ResponseEntity<Map<String, String>> {
        authService.sendCode(body.phone)
        return ResponseEntity.ok(mapOf("message" to "인증코드가 발송되었습니다."))
    }

    /** 2단계: 인증코드 검증 + 로그인/회원가입 */
    @PostMapping("/verify")
    fun verify(@RequestBody body: VerifyRequest, session: HttpSession): ResponseEntity<UserResponse> {
        if (!authService.verifyCode(body.phone, body.code)) {
            return ResponseEntity.badRequest().build()
        }
        val user = authService.loginOrRegister(body.phone, body.name)
        session.setAttribute("userId", user.id)
        return ResponseEntity.ok(UserResponse(user.id, user.name, user.phone, user.email, user.role.name))
    }

    /** 현재 로그인 유저 확인 */
    @GetMapping("/me")
    fun me(session: HttpSession): ResponseEntity<UserResponse> {
        val userId = session.getAttribute("userId") as? Long
            ?: return ResponseEntity.status(401).build()
        val user = userService.findById(userId)
        return ResponseEntity.ok(UserResponse(user.id, user.name, user.phone, user.email, user.role.name))
    }

    /** 로그아웃 */
    @PostMapping("/logout")
    fun logout(session: HttpSession): ResponseEntity<Map<String, String>> {
        session.invalidate()
        return ResponseEntity.ok(mapOf("message" to "로그아웃 되었습니다."))
    }
}

class SendCodeRequest { var phone: String = "" }
class VerifyRequest { var phone: String = ""; var code: String = ""; var name: String = "" }
