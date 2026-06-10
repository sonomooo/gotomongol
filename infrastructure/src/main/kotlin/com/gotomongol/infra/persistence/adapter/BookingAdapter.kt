package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.BookingPort
import com.gotomongol.domain.tour.Booking
import com.gotomongol.domain.tour.BookingStatus as DomainBookingStatus
import com.gotomongol.infra.persistence.entity.BookingEntity
import com.gotomongol.infra.persistence.entity.BookingStatus
import com.gotomongol.infra.persistence.repository.JpaBookingRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class BookingAdapter(private val repo: JpaBookingRepository) : BookingPort {

    override fun save(booking: Booking): Booking = repo.save(booking.toEntity()).toDomain()

    override fun findOverlapping(start: LocalDate, end: LocalDate): List<Booking> =
        repo.findOverlapping(start, end).map { it.toDomain() }

    override fun findByStartDateBetween(from: LocalDate, to: LocalDate): List<Booking> =
        repo.findByStartDateBetween(from, to).map { it.toDomain() }

    override fun findByUserIdOrderByStartDateDesc(userId: Long): List<Booking> =
        repo.findByUserIdOrderByStartDateDesc(userId).map { it.toDomain() }

    private fun BookingEntity.toDomain() = Booking(
        id = id, userId = userId, tourName = tourName, startDate = startDate,
        endDate = endDate, groupSize = groupSize, spots = spots, phone = phone,
        customerName = customerName, status = DomainBookingStatus.valueOf(status.name)
    )

    private fun Booking.toEntity() = BookingEntity(
        userId = userId, tourName = tourName, startDate = startDate,
        endDate = endDate, groupSize = groupSize, spots = spots, phone = phone,
        customerName = customerName, status = BookingStatus.valueOf(status.name)
    ).also { if (id != 0L) setId(it, id) }

    private fun setId(entity: BookingEntity, id: Long) {
        val field = entity.javaClass.superclass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }
}
