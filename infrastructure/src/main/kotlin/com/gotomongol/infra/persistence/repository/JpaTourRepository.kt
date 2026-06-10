package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.TourEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaTourRepository : JpaRepository<TourEntity, Long> {
    fun findByActiveTrue(): List<TourEntity>
}
