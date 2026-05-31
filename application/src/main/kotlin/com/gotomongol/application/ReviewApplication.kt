package com.gotomongol.application

import com.gotomongol.review.domain.Review
import com.gotomongol.review.repository.ReviewRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReviewApplication(private val reviewRepository: ReviewRepository) {

    fun create(userId: Long, tourName: String, title: String, content: String, rating: Int, imageUrls: List<String>): Review {
        return reviewRepository.save(
            Review(userId = userId, tourName = tourName, title = title,
                content = content, rating = rating, imageUrls = imageUrls.toMutableList())
        )
    }

    fun findAll(): List<Review> {
        return reviewRepository.findByVisibleTrueOrderByCreatedAtDesc()
    }

    fun findByUser(userId: Long): List<Review> {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
    }

    fun delete(id: Long, userId: Long) {
        val review = reviewRepository.findById(id).orElseThrow()
        require(review.userId == userId) { "본인의 후기만 삭제할 수 있습니다." }
        review.visible = false
    }

    fun findById(id: Long): Review {
        return reviewRepository.findById(id).orElseThrow()
    }
}
