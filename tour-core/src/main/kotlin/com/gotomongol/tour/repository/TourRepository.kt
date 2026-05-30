package com.gotomongol.tour.repository

import com.gotomongol.tour.domain.Tour
import org.springframework.data.jpa.repository.JpaRepository

interface TourRepository : JpaRepository<Tour, Long> {
    fun findByActiveTrue(): List<Tour>
}
