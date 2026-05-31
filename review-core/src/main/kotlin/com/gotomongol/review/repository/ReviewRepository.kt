package com.gotomongol.review.repository

import com.gotomongol.review.domain.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByVisibleTrueOrderByCreatedAtDesc(): List<Review>
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Review>
}
