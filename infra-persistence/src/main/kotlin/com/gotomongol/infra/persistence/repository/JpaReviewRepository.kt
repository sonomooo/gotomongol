package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.ReviewEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findByVisibleTrueOrderByCreatedAtDesc(): List<ReviewEntity>
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<ReviewEntity>
}
