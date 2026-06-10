package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.ConfirmedTripEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaConfirmedTripRepository : JpaRepository<ConfirmedTripEntity, Long> {
    fun findByUserIdOrderByStartDateDesc(userId: Long): List<ConfirmedTripEntity>
}
