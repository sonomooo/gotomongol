package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.ConfirmedTripPort
import com.gotomongol.domain.tour.ConfirmedTrip
import com.gotomongol.domain.tour.TripStatus as DomainTripStatus
import com.gotomongol.infra.persistence.entity.ConfirmedTripEntity
import com.gotomongol.infra.persistence.entity.TripStatus
import com.gotomongol.infra.persistence.repository.JpaConfirmedTripRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ConfirmedTripAdapter(private val repo: JpaConfirmedTripRepository) : ConfirmedTripPort {

    override fun save(trip: ConfirmedTrip): ConfirmedTrip = repo.save(trip.toEntity()).toDomain()

    override fun findById(id: Long): ConfirmedTrip? = repo.findByIdOrNull(id)?.toDomain()

    override fun findByUserIdOrderByStartDateDesc(userId: Long): List<ConfirmedTrip> =
        repo.findByUserIdOrderByStartDateDesc(userId).map { it.toDomain() }

    override fun findAll(): List<ConfirmedTrip> = repo.findAll().map { it.toDomain() }

    private fun ConfirmedTripEntity.toDomain() = ConfirmedTrip(
        id = id, userId = userId, quoteRequestId = quoteRequestId, tourName = tourName,
        startDate = startDate, endDate = endDate, groupSize = groupSize, spots = spots,
        dailySchedule = dailySchedule, meetingInfo = meetingInfo,
        status = DomainTripStatus.valueOf(status.name), guideNote = guideNote
    )

    private fun ConfirmedTrip.toEntity() = ConfirmedTripEntity(
        userId = userId, quoteRequestId = quoteRequestId, tourName = tourName,
        startDate = startDate, endDate = endDate, groupSize = groupSize, spots = spots,
        dailySchedule = dailySchedule, meetingInfo = meetingInfo,
        status = TripStatus.valueOf(status.name), guideNote = guideNote
    ).also { if (id != 0L) setId(it, id) }

    private fun setId(entity: ConfirmedTripEntity, id: Long) {
        val field = entity.javaClass.superclass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }
}
