package com.gotomongol.application

import com.gotomongol.domain.event.QuoteSubmittedEvent
import com.gotomongol.tour.domain.AccommodationType
import com.gotomongol.tour.domain.QuoteRequest
import com.gotomongol.tour.domain.QuoteStatus
import com.gotomongol.tour.repository.QuoteRequestRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class QuoteApplication(
    private val quoteRequestRepository: QuoteRequestRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun submit(
        customerName: String, phone: String, email: String?,
        days: Int, groupSize: Int, preferredDate: LocalDate?,
        spots: List<String>, activities: List<String>,
        accommodationType: AccommodationType, memo: String?
    ): QuoteRequest {
        val quote = quoteRequestRepository.save(
            QuoteRequest(
                customerName = customerName, phone = phone, email = email,
                days = days, groupSize = groupSize, preferredDate = preferredDate,
                spots = spots, activities = activities,
                accommodationType = accommodationType, memo = memo
            )
        )
        eventPublisher.publishEvent(QuoteSubmittedEvent(quote.id, phone, customerName))
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
