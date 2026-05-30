package com.gotomongol.domain.event

// 견적 신청됨
data class QuoteSubmittedEvent(
    val quoteId: Long,
    val phone: String,
    val name: String
)

// 여행 확정됨
data class TripConfirmedEvent(
    val tripId: Long,
    val userId: Long,
    val tourName: String,
    val quoteRequestId: Long? = null
)

// 인증코드 발송 요청
data class VerificationRequestedEvent(
    val phone: String,
    val code: String
)

// 예약 생성됨
data class BookingCreatedEvent(
    val bookingId: Long,
    val phone: String,
    val customerName: String,
    val tourName: String
)
