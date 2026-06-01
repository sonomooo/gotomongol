package com.gotomongol.application

import com.gotomongol.domain.port.ReviewPort
import com.gotomongol.domain.review.Review
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReviewApplication(private val reviewPort: ReviewPort) {

    fun create(userId: Long, tourName: String, title: String, content: String, rating: Int, imageUrls: List<String>): Review {
        return reviewPort.save(
            Review(userId = userId, tourName = tourName, title = title,
                content = content, rating = rating, imageUrls = imageUrls)
        )
    }

    fun findAll(): List<Review> {
        return reviewPort.findByVisibleTrue()
    }

    fun findByUser(userId: Long): List<Review> {
        return reviewPort.findByUserId(userId)
    }

    fun delete(id: Long, userId: Long) {
        val review = reviewPort.findById(id) ?: throw IllegalArgumentException("후기를 찾을 수 없습니다.")
        require(review.userId == userId) { "본인의 후기만 삭제할 수 있습니다." }
        reviewPort.save(review.copy(visible = false))
    }

    fun findById(id: Long): Review {
        return reviewPort.findById(id) ?: throw IllegalArgumentException("후기를 찾을 수 없습니다.")
    }
}
