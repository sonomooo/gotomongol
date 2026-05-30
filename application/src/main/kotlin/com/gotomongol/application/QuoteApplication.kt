package com.gotomongol.application

import com.gotomongol.application.dto.QuoteSubmitCommand
import com.gotomongol.domain.event.QuoteSubmittedEvent
import com.gotomongol.tour.domain.AccommodationType
import com.gotomongol.tour.domain.QuoteRequest
import com.gotomongol.tour.domain.QuoteStatus
import com.gotomongol.tour.repository.QuoteRequestRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuoteApplication(
    private val quoteRequestRepository: QuoteRequestRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun submit(cmd: QuoteSubmitCommand): QuoteRequest {
        val quote = quoteRequestRepository.save(
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
        val quote = quoteRequestRepository.findById(id).orElseThrow()
        quote.status = status
        quoteRequestRepository.save(quote)
    }

    fun findAll() = quoteRequestRepository.findAll()
    fun findByStatus(status: QuoteStatus) = quoteRequestRepository.findByStatus(status)
}
