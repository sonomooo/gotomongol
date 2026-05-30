package com.mongoltour.repository

import com.mongoltour.domain.Tour
import org.springframework.data.jpa.repository.JpaRepository

interface TourRepository : JpaRepository<Tour, Long> {
    fun findByActiveTrue(): List<Tour>
}
