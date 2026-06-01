package com.gotomongol.domain.tour

import java.time.LocalDate

data class ConfirmedTrip(
    val id: Long = 0,
    val userId: Long,
    val quoteRequestId: Long? = null,
    val tourName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val groupSize: Int,
    val spots: String = "",
    val dailySchedule: String = "",
    val meetingInfo: String? = null,
    val status: TripStatus = TripStatus.UPCOMING,
    val guideNote: String? = null
)

enum class TripStatus {
    UPCOMING, IN_PROGRESS, COMPLETED
}
