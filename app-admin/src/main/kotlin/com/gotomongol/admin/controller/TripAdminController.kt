package com.gotomongol.admin.controller

import com.gotomongol.tour.domain.ConfirmedTrip
import com.gotomongol.tour.repository.ConfirmedTripRepository
import com.gotomongol.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
@RequestMapping("/trips")
class TripAdminController(
    private val confirmedTripRepository: ConfirmedTripRepository,
    private val userService: UserService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("trips", confirmedTripRepository.findAll())
        return "trips"
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("trip", confirmedTripRepository.findById(id).orElseThrow())
        return "trip-detail"
    }

    @PostMapping
    @ResponseBody
    fun create(@RequestBody body: CreateTripRequest): ResponseEntity<Any> {
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
