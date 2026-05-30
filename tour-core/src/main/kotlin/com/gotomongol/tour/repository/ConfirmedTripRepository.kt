package com.gotomongol.tour.repository

import com.gotomongol.tour.domain.ConfirmedTrip
import org.springframework.data.jpa.repository.JpaRepository

interface ConfirmedTripRepository : JpaRepository<ConfirmedTrip, Long> {
    fun findByUserIdOrderByStartDateDesc(userId: Long): List<ConfirmedTrip>
}
