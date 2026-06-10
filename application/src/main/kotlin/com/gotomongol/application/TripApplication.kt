package com.gotomongol.application

import com.gotomongol.application.dto.BookingCommand
import com.gotomongol.application.dto.CalendarFile
import com.gotomongol.application.dto.TripRegisterCommand
import com.gotomongol.application.dto.TripResult
import com.gotomongol.domain.event.BookingCreatedEvent
import com.gotomongol.domain.event.TripConfirmedEvent
import com.gotomongol.domain.port.*
import com.gotomongol.domain.tour.*
import com.gotomongol.application.UserService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Transactional
class TripApplication(
    private val confirmedTripPort: ConfirmedTripPort,
    private val bookingPort: BookingPort,
    private val quoteRequestPort: QuoteRequestPort,
    private val tourPort: TourPort,
    private val userService: UserService,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun registerTrip(cmd: TripRegisterCommand): TripResult {
        val user = if (cmd.phone != null) {
            userService.findOrCreate(cmd.phone, cmd.customerName ?: "")
        } else {
            userService.findById(cmd.userId)
        }
        val trip = confirmedTripPort.save(
            ConfirmedTrip(
                userId = user.id, quoteRequestId = cmd.quoteRequestId,
                tourName = cmd.tourName, startDate = cmd.startDate, endDate = cmd.endDate,
                groupSize = cmd.groupSize, spots = cmd.spots,
                dailySchedule = cmd.dailySchedule, meetingInfo = cmd.meetingInfo, guideNote = cmd.guideNote
            )
        )
        cmd.quoteRequestId?.let { qid ->
            quoteRequestPort.updateStatus(qid, QuoteStatus.CONFIRMED)
        }
        eventPublisher.publishEvent(TripConfirmedEvent(trip.id, user.id, cmd.tourName, cmd.quoteRequestId))
        return TripResult(trip.id, user.name, "${user.name}님 여행 등록 완료")
    }

    fun createBooking(cmd: BookingCommand): Booking {
        val overlapping = bookingPort.findOverlapping(cmd.startDate, cmd.endDate)
        require(overlapping.isEmpty()) { "해당 기간에 이미 예약이 있습니다." }
        val user = userService.findOrCreate(cmd.phone, cmd.customerName)
        val booking = bookingPort.save(
            Booking(userId = user.id, tourName = cmd.tourName, startDate = cmd.startDate,
                endDate = cmd.endDate, groupSize = cmd.groupSize, phone = cmd.phone, customerName = cmd.customerName)
        )
        eventPublisher.publishEvent(BookingCreatedEvent(booking.id, cmd.phone, cmd.customerName, cmd.tourName))
        return booking
    }

    fun generateCalendar(tripId: Long): CalendarFile {
        val trip = confirmedTripPort.findById(tripId) ?: throw IllegalArgumentException("여행을 찾을 수 없습니다.")
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

    fun getUnavailableDates(from: LocalDate, to: LocalDate): List<String> {
        return bookingPort.findByStartDateBetween(from, to).flatMap { b ->
            generateSequence(b.startDate) { it.plusDays(1) }
                .takeWhile { !it.isAfter(b.endDate) }
                .map { it.toString() }.toList()
        }.distinct()
    }

    fun findTripsByUser(userId: Long): List<ConfirmedTrip> {
        return confirmedTripPort.findByUserIdOrderByStartDateDesc(userId)
    }

    fun findTripById(id: Long): ConfirmedTrip {
        return confirmedTripPort.findById(id) ?: throw IllegalArgumentException("여행을 찾을 수 없습니다.")
    }

    fun findQuoteByTripId(tripId: Long): QuoteRequest? {
        val trip = findTripById(tripId)
        return trip.quoteRequestId?.let { quoteRequestPort.findById(it) }
    }

    fun findAllTrips(): List<ConfirmedTrip> {
        return confirmedTripPort.findAll()
    }

    fun findActiveTours(): List<Tour> {
        return tourPort.findByActiveTrue()
    }

    fun findTourById(id: Long): Tour {
        return tourPort.findById(id) ?: throw IllegalArgumentException("투어를 찾을 수 없습니다.")
    }
}
