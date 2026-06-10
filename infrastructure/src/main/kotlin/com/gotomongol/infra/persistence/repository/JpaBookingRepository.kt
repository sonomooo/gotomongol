package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.BookingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface JpaBookingRepository : JpaRepository<BookingEntity, Long> {
    fun findByUserIdOrderByStartDateDesc(userId: Long): List<BookingEntity>

    @Query("SELECT b FROM BookingEntity b WHERE b.status != 'CANCELLED' AND b.startDate <= :end AND b.endDate >= :start")
    fun findOverlapping(@Param("start") start: LocalDate, @Param("end") end: LocalDate): List<BookingEntity>

    @Query("SELECT b FROM BookingEntity b WHERE b.status != 'CANCELLED' AND b.startDate >= :from AND b.startDate <= :to")
    fun findByStartDateBetween(@Param("from") from: LocalDate, @Param("to") to: LocalDate): List<BookingEntity>
}
