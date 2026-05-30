package com.gotomongol.controller

import com.gotomongol.tour.domain.ConfirmedTrip
import com.gotomongol.tour.domain.TripStatus
import com.gotomongol.tour.repository.ConfirmedTripRepository
import com.gotomongol.user.service.UserService
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller
class TripController(
    private val confirmedTripRepository: ConfirmedTripRepository,
    private val userService: UserService
) {

    /** 내 여행 목록 (로그인 유저) */
    @GetMapping("/my/trips")
    fun myTrips(session: HttpSession, model: Model): String {
        val userId = session.getAttribute("userId") as? Long
            ?: return "redirect:/login"
        model.addAttribute("trips", confirmedTripRepository.findByUserIdOrderByStartDateDesc(userId))
        model.addAttribute("userName", userService.findById(userId).name)
        return "my-trips"
    }

    /** 여행 상세 일정 */
    @GetMapping("/my/trips/{id}")
    fun tripDetail(@PathVariable id: Long, session: HttpSession, model: Model): String {
        val userId = session.getAttribute("userId") as? Long
            ?: return "redirect:/login"
        val trip = confirmedTripRepository.findById(id).orElseThrow()
        if (trip.userId != userId) return "redirect:/my/trips"
        model.addAttribute("trip", trip)
        return "trip-detail"
    }

    /** ICS 캘린더 파일 다운로드 */
    @GetMapping("/my/trips/{id}/calendar")
    @ResponseBody
    fun downloadIcs(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val trip = confirmedTripRepository.findById(id).orElseThrow()
        val ics = buildIcs(trip)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=trip-${trip.id}.ics")
            .contentType(MediaType.parseMediaType("text/calendar"))
            .body(ics.toByteArray())
    }

    /** 어드민: 확정 여행 등록 */
    @PostMapping("/admin/trips")
    @ResponseBody
    fun createTrip(@RequestBody body: CreateTripRequest): ResponseEntity<Any> {
        val user = userService.findById(body.userId)
        val trip = confirmedTripRepository.save(
            ConfirmedTrip(
                userId = body.userId,
                tourName = body.tourName,
                startDate = LocalDate.parse(body.startDate),
                endDate = LocalDate.parse(body.endDate),
                groupSize = body.groupSize,
                spots = body.spots,
                dailySchedule = body.dailySchedule,
                meetingInfo = body.meetingInfo,
                guideNote = body.guideNote
            )
        )
        return ResponseEntity.ok(mapOf("id" to trip.id, "message" to "${user.name}님 여행 등록 완료"))
    }

    /** 어드민: 확정 여행 목록 */
    @GetMapping("/admin/trips")
    fun adminTrips(model: Model): String {
        model.addAttribute("trips", confirmedTripRepository.findAll())
        return "admin/trips"
    }

    private fun buildIcs(trip: ConfirmedTrip): String {
        val dtStart = trip.startDate.format(DateTimeFormatter.BASIC_ISO_DATE)
        val dtEnd = trip.endDate.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE)
        return """
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
END:VCALENDAR
        """.trimIndent()
    }
}

class CreateTripRequest {
    var userId: Long = 0
    var tourName: String = ""
    var startDate: String = ""
    var endDate: String = ""
    var groupSize: Int = 2
    var spots: String = ""
    var dailySchedule: String = ""
    var meetingInfo: String? = null
    var guideNote: String? = null
}
