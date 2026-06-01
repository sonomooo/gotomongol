package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.TourPort
import com.gotomongol.domain.tour.Tour
import com.gotomongol.infra.persistence.entity.TourEntity
import com.gotomongol.infra.persistence.repository.JpaTourRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class TourAdapter(private val repo: JpaTourRepository) : TourPort {

    override fun findByActiveTrue(): List<Tour> = repo.findByActiveTrue().map { it.toDomain() }

    override fun findById(id: Long): Tour? = repo.findByIdOrNull(id)?.toDomain()

    override fun findAll(): List<Tour> = repo.findAll().map { it.toDomain() }

    override fun save(tour: Tour): Tour = repo.save(tour.toEntity()).toDomain()

    override fun count(): Long = repo.count()

    private fun TourEntity.toDomain() = Tour(
        id = id, name = name, days = days, description = description,
        minPrice = minPrice, maxPrice = maxPrice, spots = spots,
        activities = activities, imageUrl = imageUrl, imageUrls = imageUrls,
        detailContent = detailContent, active = active
    )

    private fun Tour.toEntity() = TourEntity(
        name = name, days = days, description = description,
        minPrice = minPrice, maxPrice = maxPrice, spots = spots,
        activities = activities, imageUrl = imageUrl, imageUrls = imageUrls,
        detailContent = detailContent, active = active
    ).also { if (id != 0L) setId(it, id) }

    private fun setId(entity: TourEntity, id: Long) {
        val field = entity.javaClass.superclass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }
}
