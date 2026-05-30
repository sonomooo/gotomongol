package com.gotomongol.application

import com.gotomongol.domain.event.BookingCreatedEvent
import com.gotomongol.domain.event.TripConfirmedEvent
import com.gotomongol.tour.domain.Booking
import com.gotomongol.tour.domain.ConfirmedTrip
import com.gotomongol.tour.domain.QuoteStatus
import com.gotomongol.tour.repository.BookingRepository
import com.gotomongol.tour.repository.ConfirmedTripRepository
import com.gotomongol.tour.repository.QuoteRequestRepository
import com.gotomongol.tour.repository.TourRepository
import com.gotomongol.user.service.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class TripApplication(
    private val confirmedTripRepository: ConfirmedTripRepository,
    private val bookingRepository: BookingRepository,
    private val quoteRequestRepository: QuoteRequestRepository,
    private val tourRepository: TourRepository,
    private val userService: UserService,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun registerTrip(
        userId: Long, quoteRequestId: Long?, tourName: String,
        startDate: LocalDate, endDate: LocalDate, groupSize: Int,
        spots: String, dailySchedule: String, meetingInfo: String?, guideNote: String?
    ): ConfirmedTrip {
        val trip = confirmedTripRepository.save(
            ConfirmedTrip(
                userId = userId, quoteRequestId = quoteRequestId, tourName = tourName,
                startDate = startDate, endDate = endDate, groupSize = groupSize,
                spots = spots, dailySchedule = dailySchedule,
                meetingInfo = meetingInfo, guideNote = guideNote
            )
        )
        // 연결된 견적 상태 변경
        quoteRequestId?.let { qid ->
            quoteRequestRepository.findById(qid).ifPresent {
                it.status = QuoteStatus.CONFIRMED
                quoteRequestRepository.save(it)
            }
        }
        eventPublisher.publishEvent(TripConfirmedEvent(trip.id, userId, tourName, quoteRequestId))
        return trip
    }

    fun createBooking(
        customerName: String, phone: String, tourName: String,
        startDate: LocalDate, endDate: LocalDate, groupSize: Int
    ): Booking {
        val overlapping = bookingRepository.findOverlapping(startDate, endDate)
        require(overlapping.isEmpty()) { "해당 기간에 이미 예약이 있습니다." }

        val user = userService.findOrCreate(phone, customerName)
        val booking = bookingRepository.save(
            Booking(userId = user.id, tourName = tourName, startDate = startDate,
                endDate = endDate, groupSize = groupSize, phone = phone, customerName = customerName)
        )
        eventPublisher.publishEvent(BookingCreatedEvent(booking.id, phone, customerName, tourName))
        return booking
    }

    fun getUnavailableDates(from: LocalDate, to: LocalDate): List<String> {
        return bookingRepository.findByStartDateBetween(from, to).flatMap { b ->
            generateSequence(b.startDate) { it.plusDays(1) }
                .takeWhile { !it.isAfter(b.endDate) }
                .map { it.toString() }.toList()
        }.distinct()
    }

    fun findTripsByUser(userId: Long) = confirmedTripRepository.findByUserIdOrderByStartDateDesc(userId)
    fun findTripById(id: Long) = confirmedTripRepository.findById(id).orElseThrow()
    fun findAllTrips() = confirmedTripRepository.findAll()
    fun findActiveTours() = tourRepository.findByActiveTrue()
    fun findTourById(id: Long) = tourRepository.findById(id).orElseThrow()
}
