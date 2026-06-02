package com.gotomongol.domain.port

import com.gotomongol.domain.review.Review
import com.gotomongol.domain.tour.*
import com.gotomongol.domain.user.User
import com.gotomongol.domain.user.VerificationCode
import java.time.LocalDate

interface UserPort {
    fun findByPhone(phone: String): User?
    fun findById(id: Long): User?
    fun save(user: User): User
    fun existsByPhone(phone: String): Boolean
    fun count(): Long
}

interface VerificationCodePort {
    fun save(code: VerificationCode): VerificationCode
    fun findLatestByPhone(phone: String): VerificationCode?
    fun markVerified(id: Long)
}

interface TourPort {
    fun findByActiveTrue(): List<Tour>
    fun findById(id: Long): Tour?
    fun findAll(): List<Tour>
    fun save(tour: Tour): Tour
    fun count(): Long
}

interface QuoteRequestPort {
    fun save(quote: QuoteRequest): QuoteRequest
    fun findById(id: Long): QuoteRequest?
    fun findAll(): List<QuoteRequest>
    fun findByStatus(status: QuoteStatus): List<QuoteRequest>
    fun updateStatus(id: Long, status: QuoteStatus)
}

interface BookingPort {
    fun save(booking: Booking): Booking
    fun findOverlapping(start: LocalDate, end: LocalDate): List<Booking>
    fun findByStartDateBetween(from: LocalDate, to: LocalDate): List<Booking>
    fun findByUserIdOrderByStartDateDesc(userId: Long): List<Booking>
}

interface ConfirmedTripPort {
    fun save(trip: ConfirmedTrip): ConfirmedTrip
    fun findById(id: Long): ConfirmedTrip?
    fun findByUserIdOrderByStartDateDesc(userId: Long): List<ConfirmedTrip>
    fun findAll(): List<ConfirmedTrip>
}

interface SiteConfigPort {
    fun findByKey(key: String): SiteConfig?
    fun save(config: SiteConfig): SiteConfig
    fun findAll(): List<SiteConfig>
}

interface ReviewPort {
    fun save(review: Review): Review
    fun findByVisibleTrue(): List<Review>
    fun findByUserId(userId: Long): List<Review>
    fun findById(id: Long): Review?
}

interface PriceConfigPort {
    fun findAll(): List<PriceConfig>
    fun findByCategory(category: PriceCategory): List<PriceConfig>
    fun save(config: PriceConfig): PriceConfig
    fun deleteById(id: Long)
}

interface SpotItemPort {
    fun findAll(): List<SpotItem>
    fun findActive(): List<SpotItem>
    fun save(item: SpotItem): SpotItem
    fun deleteById(id: Long)
}

interface ActivityItemPort {
    fun findAll(): List<ActivityItem>
    fun findActive(): List<ActivityItem>
    fun save(item: ActivityItem): ActivityItem
    fun deleteById(id: Long)
}
