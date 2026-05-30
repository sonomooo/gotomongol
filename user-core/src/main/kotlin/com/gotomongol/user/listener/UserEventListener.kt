package com.gotomongol.user.listener

import com.gotomongol.domain.event.QuoteSubmittedEvent
import com.gotomongol.domain.event.BookingCreatedEvent
import com.gotomongol.user.service.UserService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class UserEventListener(private val userService: UserService) {

    @EventListener
    fun onQuoteSubmitted(event: QuoteSubmittedEvent) {
        userService.findOrCreate(event.phone, event.name)
    }

    @EventListener
    fun onBookingCreated(event: BookingCreatedEvent) {
        userService.findOrCreate(event.phone, event.customerName)
    }
}
