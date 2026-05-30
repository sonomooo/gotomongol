package com.mongoltour.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "quote_requests")
class QuoteRequest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val customerName: String,
    val phone: String,
    val email: String? = null,

    val days: Int,
    val groupSize: Int,
    val preferredDate: LocalDate? = null,

    @ElementCollection
    @CollectionTable(name = "quote_spots", joinColumns = [JoinColumn(name = "quote_id")])
    @Column(name = "spot")
    val spots: List<String> = emptyList(),

    @ElementCollection
    @CollectionTable(name = "quote_activities", joinColumns = [JoinColumn(name = "quote_id")])
    @Column(name = "activity")
    val activities: List<String> = emptyList(),

    @Enumerated(EnumType.STRING)
    val accommodationType: AccommodationType = AccommodationType.CAMP,

    val memo: String? = null,

    @Enumerated(EnumType.STRING)
    var status: QuoteStatus = QuoteStatus.PENDING,

    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class AccommodationType {
    CAMP, PREMIUM_CAMP, HOTEL_MIX
}

enum class QuoteStatus {
    PENDING, QUOTED, CONFIRMED, CANCELLED
}
