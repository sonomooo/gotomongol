package com.gotomongol.config

import com.gotomongol.domain.response.ServiceErrorType
import com.gotomongol.domain.response.ServiceResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ServiceResponse<Nothing> {
        log.warn("[BAD_REQUEST] {}", e.message)
        return ServiceResponse.error(ServiceErrorType.BAD_REQUEST, e.message)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ServiceResponse<Nothing> {
        log.warn("[NOT_FOUND] {}", e.message)
        return ServiceResponse.error(ServiceErrorType.NOT_FOUND, e.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ServiceResponse<Nothing> {
        log.error("[INTERNAL_ERROR]", e)
        return ServiceResponse.error(ServiceErrorType.INTERNAL_ERROR, e.message)
    }
}
