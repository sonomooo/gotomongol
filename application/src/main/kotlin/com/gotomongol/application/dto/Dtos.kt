package com.gotomongol.application.dto

import java.time.LocalDate

// ─── 견적 ───
data class QuoteSubmitCommand(
    val customerName: String,
    val phone: String,
    val email: String?,
    val days: Int,
    val groupSize: Int,
    val preferredDate: LocalDate?,
    val spots: List<String>,
    val activities: List<String>,
    val accommodationType: String,
    val memo: String?
)

// ─── 예약 ───
data class BookingCommand(
    val customerName: String,
    val phone: String,
    val tourName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val groupSize: Int
)

// ─── 여행 등록 ───
data class TripRegisterCommand(
    val userId: Long = 0,
    val phone: String? = null,
    val customerName: String? = null,
    val quoteRequestId: Long?,
    val tourName: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val groupSize: Int,
    val spots: String,
    val dailySchedule: String,
    val meetingInfo: String?,
    val guideNote: String?
)

// ─── 응답 ───
data class TripResult(
    val id: Long,
    val userName: String,
    val message: String
)

data class CalendarFile(
    val filename: String,
    val content: ByteArray
)
