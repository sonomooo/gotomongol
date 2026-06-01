package com.gotomongol.domain.tour

import java.time.LocalDate

data class QuoteRequest(
    val id: Long = 0,
    val customerName: String,
    val phone: String,
    val email: String? = null,
    val days: Int,
    val groupSize: Int,
    val preferredDate: LocalDate? = null,
    val spots: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val accommodationType: AccommodationType = AccommodationType.CAMP,
    val memo: String? = null,
    val status: QuoteStatus = QuoteStatus.PENDING
)

enum class AccommodationType {
    CAMP, PREMIUM_CAMP, HOTEL_MIX
}

enum class QuoteStatus {
    PENDING, QUOTED, CONFIRMED, CANCELLED
}
