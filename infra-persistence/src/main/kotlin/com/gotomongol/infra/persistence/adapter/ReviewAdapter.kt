package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.ReviewPort
import com.gotomongol.domain.review.Review
import com.gotomongol.infra.persistence.entity.ReviewEntity
import com.gotomongol.infra.persistence.repository.JpaReviewRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ReviewAdapter(private val repo: JpaReviewRepository) : ReviewPort {

    override fun save(review: Review): Review = repo.save(review.toEntity()).toDomain()

    override fun findByVisibleTrue(): List<Review> =
        repo.findByVisibleTrueOrderByCreatedAtDesc().map { it.toDomain() }

    override fun findByUserId(userId: Long): List<Review> =
        repo.findByUserIdOrderByCreatedAtDesc(userId).map { it.toDomain() }

    override fun findById(id: Long): Review? = repo.findByIdOrNull(id)?.toDomain()

    private fun ReviewEntity.toDomain() = Review(
        id = id, userId = userId, tourName = tourName, title = title,
        content = content, rating = rating, imageUrls = imageUrls, visible = visible
    )

    private fun Review.toEntity() = ReviewEntity(
        userId = userId, tourName = tourName, title = title,
        content = content, rating = rating, imageUrls = imageUrls, visible = visible
    ).also { if (id != 0L) setId(it, id) }

    private fun setId(entity: ReviewEntity, id: Long) {
        val field = entity.javaClass.superclass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }
}
