package com.gotomongol.domain.response

data class ServiceResponse<T>(
    var code: String,
    var payload: T? = null,
    var message: String? = null
) {
    companion object {
        fun <T> success(payload: T? = null) = ServiceResponse("SUCCESS", payload)
        fun <T> error(errorType: ServiceErrorType) = ServiceResponse<T>(
            code = errorType.name,
            message = errorType.message
        )
        fun <T> error(errorType: ServiceErrorType, message: String?) = ServiceResponse<T>(
            code = errorType.name,
            message = message ?: errorType.message
        )
    }
}

enum class ServiceErrorType(val message: String) {
    UNAUTHORIZED("로그인이 필요합니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    NOT_FOUND("데이터를 찾을 수 없습니다."),
    CONFLICT("이미 존재하는 데이터입니다."),
    VERIFICATION_FAILED("인증에 실패했습니다."),
    BOOKING_CONFLICT("해당 기간에 이미 예약이 있습니다.")
}
