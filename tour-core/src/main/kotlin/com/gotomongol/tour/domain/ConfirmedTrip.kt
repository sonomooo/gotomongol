package com.gotomongol.tour.domain

import com.gotomongol.domain.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "confirmed_trips")
class ConfirmedTrip(
    val userId: Long,
    val tourName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val groupSize: Int,
    val spots: String = "",

    @Column(columnDefinition = "TEXT")
    var dailySchedule: String = "",

    var meetingInfo: String? = null,

    @Enumerated(EnumType.STRING)
    var status: TripStatus = TripStatus.UPCOMING,

    @Column(columnDefinition = "TEXT")
    var guideNote: String? = null
) : BaseEntity()

enum class TripStatus {
    UPCOMING, IN_PROGRESS, COMPLETED
}
