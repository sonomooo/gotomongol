package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.QuoteRequestEntity
import com.gotomongol.infra.persistence.entity.QuoteStatus
import org.springframework.data.jpa.repository.JpaRepository

interface JpaQuoteRequestRepository : JpaRepository<QuoteRequestEntity, Long> {
    fun findByStatus(status: QuoteStatus): List<QuoteRequestEntity>
}
