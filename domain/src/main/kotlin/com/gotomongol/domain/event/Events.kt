package com.gotomongol.domain.event

data class QuoteSubmittedEvent(val quoteId: Long, val phone: String, val name: String)
data class TripConfirmedEvent(val tripId: Long, val userId: Long, val tourName: String, val quoteRequestId: Long? = null)
data class VerificationRequestedEvent(val phone: String, val code: String)
data class BookingCreatedEvent(val bookingId: Long, val phone: String, val customerName: String, val tourName: String)
