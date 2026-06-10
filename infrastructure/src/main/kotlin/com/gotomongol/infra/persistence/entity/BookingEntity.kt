package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "bookings")
class BookingEntity(
    val userId: Long,
    val tourName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val groupSize: Int,
    val spots: String = "",
    val phone: String,
    val customerName: String,

    @Enumerated(EnumType.STRING)
    var status: BookingStatus = BookingStatus.PENDING
) : BaseEntity()

enum class BookingStatus {
    PENDING, CONFIRMED, CANCELLED
}
