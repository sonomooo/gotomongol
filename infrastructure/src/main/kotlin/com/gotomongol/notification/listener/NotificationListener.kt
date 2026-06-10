package com.gotomongol.notification.listener

import com.gotomongol.domain.event.BookingCreatedEvent
import com.gotomongol.domain.event.QuoteSubmittedEvent
import com.gotomongol.domain.event.TripConfirmedEvent
import com.gotomongol.domain.event.VerificationRequestedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NotificationListener {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener
    fun onVerificationRequested(event: VerificationRequestedEvent) {
        // TODO: 실제 SMS 발송 (CoolSMS, NHN Cloud 등)
        log.info("[SMS] {} → 인증코드: {}", event.phone, event.code)
    }

    @EventListener
    fun onQuoteSubmitted(event: QuoteSubmittedEvent) {
        // TODO: 어드민에게 카카오톡 알림
        log.info("[알림] 새 견적 요청: {} ({})", event.name, event.phone)
    }

    @EventListener
    fun onTripConfirmed(event: TripConfirmedEvent) {
        // TODO: 고객에게 여행 확정 알림톡
        log.info("[알림] 여행 확정: userId={}, tour={}", event.userId, event.tourName)
    }

    @EventListener
    fun onBookingCreated(event: BookingCreatedEvent) {
        // TODO: 어드민에게 새 예약 알림
        log.info("[알림] 새 예약: {} - {}", event.customerName, event.tourName)
    }
}
