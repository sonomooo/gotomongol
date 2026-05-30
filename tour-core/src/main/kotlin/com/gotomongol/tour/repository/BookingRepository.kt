package com.gotomongol.tour.repository

import com.gotomongol.tour.domain.Booking
import com.gotomongol.tour.domain.BookingStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface BookingRepository : JpaRepository<Booking, Long> {
    fun findByUserIdOrderByStartDateDesc(userId: Long): List<Booking>
    fun findByPhone(phone: String): List<Booking>

    @Query("SELECT b FROM Booking b WHERE b.status != 'CANCELLED' AND b.startDate <= :end AND b.endDate >= :start")
    fun findOverlapping(start: LocalDate, end: LocalDate): List<Booking>

    @Query("SELECT b FROM Booking b WHERE b.status != 'CANCELLED' AND b.startDate >= :from AND b.startDate <= :to")
    fun findByStartDateBetween(from: LocalDate, to: LocalDate): List<Booking>
}
