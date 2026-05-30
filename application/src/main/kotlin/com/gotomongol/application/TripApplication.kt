package com.gotomongol.application

import com.gotomongol.application.dto.BookingCommand
import com.gotomongol.application.dto.CalendarFile
import com.gotomongol.application.dto.TripRegisterCommand
import com.gotomongol.application.dto.TripResult
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
import java.time.format.DateTimeFormatter

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

    fun registerTrip(cmd: TripRegisterCommand): TripResult {
        val user = userService.findById(cmd.userId)
        val trip = confirmedTripRepository.save(
            ConfirmedTrip(
                userId = cmd.userId, quoteRequestId = cmd.quoteRequestId,
                tourName = cmd.tourName, startDate = cmd.startDate, endDate = cmd.endDate,
                groupSize = cmd.groupSize, spots = cmd.spots,
                dailySchedule = cmd.dailySchedule, meetingInfo = cmd.meetingInfo, guideNote = cmd.guideNote
            )
        )
        cmd.quoteRequestId?.let { qid ->
            quoteRequestRepository.findById(qid).ifPresent {
                it.status = QuoteStatus.CONFIRMED
                quoteRequestRepository.save(it)
            }
        }
        eventPublisher.publishEvent(TripConfirmedEvent(trip.id, cmd.userId, cmd.tourName, cmd.quoteRequestId))
        return TripResult(trip.id, user.name, "${user.name}님 여행 등록 완료")
    }

    fun createBooking(cmd: BookingCommand): Booking {
        val overlapping = bookingRepository.findOverlapping(cmd.startDate, cmd.endDate)
        require(overlapping.isEmpty()) { "해당 기간에 이미 예약이 있습니다." }
        val user = userService.findOrCreate(cmd.phone, cmd.customerName)
        val booking = bookingRepository.save(
            Booking(userId = user.id, tourName = cmd.tourName, startDate = cmd.startDate,
                endDate = cmd.endDate, groupSize = cmd.groupSize, phone = cmd.phone, customerName = cmd.customerName)
        )
        eventPublisher.publishEvent(BookingCreatedEvent(booking.id, cmd.phone, cmd.customerName, cmd.tourName))
        return booking
    }

    fun generateCalendar(tripId: Long): CalendarFile {
        val trip = confirmedTripRepository.findById(tripId).orElseThrow()
        val dtStart = trip.startDate.format(DateTimeFormatter.BASIC_ISO_DATE)
        val dtEnd = trip.endDate.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)
        val ics = """
BEGIN:VCALENDAR
VERSION:2.0
PRODID:-//GoToMongol//Trip//KO
BEGIN:VEVENT
DTSTART;VALUE=DATE:$dtStart
DTEND;VALUE=DATE:$dtEnd
SUMMARY:${trip.tourName} - 고투몽골
DESCRIPTION:${trip.dailySchedule.replace("\n", "\\n")}
LOCATION:몽골
END:VEVENT
END:VCALENDAR""".trimIndent()
        return CalendarFile("trip-${trip.id}.ics", ics.toByteArray())
    }

    fun getUnavailableDates(from: LocalDate, to: LocalDate): List<String> =
        bookingRepository.findByStartDateBetween(from, to).flatMap { b ->
            generateSequence(b.startDate) { it.plusDays(1) }
                .takeWhile { !it.isAfter(b.endDate) }
                .map { it.toString() }.toList()
        }.distinct()

    fun findTripsByUser(userId: Long) = confirmedTripRepository.findByUserIdOrderByStartDateDesc(userId)
    fun findTripById(id: Long) = confirmedTripRepository.findById(id).orElseThrow()
    fun findAllTrips() = confirmedTripRepository.findAll()
    fun findActiveTours() = tourRepository.findByActiveTrue()
    fun findTourById(id: Long) = tourRepository.findById(id).orElseThrow()
}
