package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.QuoteRequestPort
import com.gotomongol.domain.tour.QuoteRequest
import com.gotomongol.domain.tour.QuoteStatus as DomainQuoteStatus
import com.gotomongol.infra.persistence.entity.AccommodationType
import com.gotomongol.infra.persistence.entity.QuoteRequestEntity
import com.gotomongol.infra.persistence.entity.QuoteStatus
import com.gotomongol.infra.persistence.repository.JpaQuoteRequestRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class QuoteRequestAdapter(private val repo: JpaQuoteRequestRepository) : QuoteRequestPort {

    override fun save(quote: QuoteRequest): QuoteRequest = repo.save(quote.toEntity()).toDomain()

    override fun findById(id: Long): QuoteRequest? = repo.findByIdOrNull(id)?.toDomain()

    override fun findAll(): List<QuoteRequest> = repo.findAll().map { it.toDomain() }

    override fun findByStatus(status: DomainQuoteStatus): List<QuoteRequest> =
        repo.findByStatus(QuoteStatus.valueOf(status.name)).map { it.toDomain() }

    override fun updateStatus(id: Long, status: DomainQuoteStatus) {
        repo.findByIdOrNull(id)?.let { it.status = QuoteStatus.valueOf(status.name); repo.save(it) }
    }

    private fun QuoteRequestEntity.toDomain() = QuoteRequest(
        id = id, customerName = customerName, phone = phone, email = email,
        days = days, groupSize = groupSize, preferredDate = preferredDate,
        spots = spots, activities = activities,
        accommodationType = com.gotomongol.domain.tour.AccommodationType.valueOf(accommodationType.name),
        memo = memo, status = DomainQuoteStatus.valueOf(status.name)
    )

    private fun QuoteRequest.toEntity() = QuoteRequestEntity(
        customerName = customerName, phone = phone, email = email,
        days = days, groupSize = groupSize, preferredDate = preferredDate,
        spots = spots, activities = activities,
        accommodationType = AccommodationType.valueOf(accommodationType.name),
        memo = memo, status = QuoteStatus.valueOf(status.name)
    ).also { if (id != 0L) setId(it, id) }

    private fun setId(entity: QuoteRequestEntity, id: Long) {
        val field = entity.javaClass.superclass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }
}
