package com.gotomongol.config

import com.gotomongol.domain.auth.Authenticated
import com.gotomongol.infra.auth.JwtTokenUtils
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor : HandlerInterceptor {

    companion object {
        const val USER_ID_ATTR = "userId"
        const val ACCESS_TOKEN_HEADER = "Authorization"
        const val REFRESH_TOKEN_COOKIE = "refreshToken"
        const val NEW_ACCESS_TOKEN_HEADER = "X-New-Access-Token"
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) return true

        val annotation = handler.getMethodAnnotation(Authenticated::class.java) ?: return true

        // Access Token 추출 (헤더 or 쿠키)
        val accessToken = extractAccessToken(request)
        val refreshToken = extractRefreshToken(request)

        // Access Token 유효 → 통과
        if (accessToken != null && JwtTokenUtils.validateToken(accessToken) && !JwtTokenUtils.isTokenExpired(accessToken)) {
            val userId = JwtTokenUtils.getUserIdFromToken(accessToken)
            request.setAttribute(USER_ID_ATTR, userId)
            return true
        }

        // Access 만료 + Refresh 유효 → 자동 갱신
        if (refreshToken != null && JwtTokenUtils.validateToken(refreshToken) && !JwtTokenUtils.isTokenExpired(refreshToken)) {
            val userId = JwtTokenUtils.getUserIdFromToken(refreshToken)
            val newAccessToken = JwtTokenUtils.generateAccessToken(userId)
            val newRefreshToken = JwtTokenUtils.generateRefreshToken(userId)

            response.setHeader(NEW_ACCESS_TOKEN_HEADER, newAccessToken)
            val cookie = Cookie(REFRESH_TOKEN_COOKIE, newRefreshToken).apply {
                isHttpOnly = true
                secure = true
                path = "/"
                maxAge = 7 * 24 * 60 * 60
                setAttribute("SameSite", "Strict")
            }
            response.addCookie(cookie)

            request.setAttribute(USER_ID_ATTR, userId)
            return true
        }

        // 인증 실패
        if (annotation.required) {
            response.status = 401
            response.contentType = "application/json"
            response.writer.write("""{"code":"UNAUTHORIZED","message":"로그인이 필요합니다."}""")
            return false
        }

        return true
    }

    private fun extractAccessToken(request: HttpServletRequest): String? {
        val header = request.getHeader(ACCESS_TOKEN_HEADER)
        if (header != null && header.startsWith("Bearer ", true)) {
            return header.substring(7)
        }
        // 쿠키 폴백
        return request.cookies?.find { it.name == "accessToken" }?.value
    }

    private fun extractRefreshToken(request: HttpServletRequest): String? {
        return request.cookies?.find { it.name == REFRESH_TOKEN_COOKIE }?.value
    }
}
