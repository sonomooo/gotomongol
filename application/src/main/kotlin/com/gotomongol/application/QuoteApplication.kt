package com.gotomongol.application

import com.gotomongol.application.dto.QuoteSubmitCommand
import com.gotomongol.domain.event.QuoteSubmittedEvent
import com.gotomongol.domain.port.QuoteRequestPort
import com.gotomongol.domain.tour.AccommodationType
import com.gotomongol.domain.tour.QuoteRequest
import com.gotomongol.domain.tour.QuoteStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuoteApplication(
    private val quoteRequestPort: QuoteRequestPort,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun submit(cmd: QuoteSubmitCommand): QuoteRequest {
        val quote = quoteRequestPort.save(
            QuoteRequest(
                customerName = cmd.customerName, phone = cmd.phone, email = cmd.email,
                days = cmd.days, groupSize = cmd.groupSize, preferredDate = cmd.preferredDate,
                spots = cmd.spots, activities = cmd.activities,
                accommodationType = AccommodationType.valueOf(cmd.accommodationType), memo = cmd.memo
            )
        )
        eventPublisher.publishEvent(QuoteSubmittedEvent(quote.id, cmd.phone, cmd.customerName))
        return quote
    }

    fun updateStatus(id: Long, status: QuoteStatus) {
        quoteRequestPort.updateStatus(id, status)
    }

    fun findAll(): List<QuoteRequest> {
        return quoteRequestPort.findAll()
    }

    fun findByStatus(status: QuoteStatus): List<QuoteRequest> {
        return quoteRequestPort.findByStatus(status)
    }
}
