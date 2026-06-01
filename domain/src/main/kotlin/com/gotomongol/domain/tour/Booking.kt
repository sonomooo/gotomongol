package com.gotomongol.domain.tour

import java.time.LocalDate

data class Booking(
    val id: Long = 0,
    val userId: Long,
    val tourName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val groupSize: Int,
    val spots: String = "",
    val phone: String,
    val customerName: String,
    val status: BookingStatus = BookingStatus.PENDING
)

enum class BookingStatus {
    PENDING, CONFIRMED, CANCELLED
}
