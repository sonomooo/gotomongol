package com.mongoltour.repository

import com.mongoltour.domain.QuoteRequest
import com.mongoltour.domain.QuoteStatus
import org.springframework.data.jpa.repository.JpaRepository

interface QuoteRequestRepository : JpaRepository<QuoteRequest, Long> {
    fun findByStatus(status: QuoteStatus): List<QuoteRequest>
}
