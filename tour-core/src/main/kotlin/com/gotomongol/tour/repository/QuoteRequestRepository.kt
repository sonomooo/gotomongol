package com.gotomongol.tour.repository

import com.gotomongol.tour.domain.QuoteRequest
import com.gotomongol.tour.domain.QuoteStatus
import org.springframework.data.jpa.repository.JpaRepository

interface QuoteRequestRepository : JpaRepository<QuoteRequest, Long> {
    fun findByStatus(status: QuoteStatus): List<QuoteRequest>
}
