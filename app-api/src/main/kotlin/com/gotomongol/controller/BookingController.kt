package com.gotomongol.controller

import com.gotomongol.tour.domain.Booking
import com.gotomongol.tour.repository.BookingRepository
import com.gotomongol.user.service.UserService
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
class BookingController(
    private val bookingRepository: BookingRepository,
    private val userService: UserService
) {

    @GetMapping("/login")
    fun loginPage(): String = "login"

    @GetMapping("/booking")
    fun bookingPage(): String = "booking"

    @GetMapping("/booking/complete")
    fun bookingComplete(): String = "booking-complete"

    @GetMapping("/my")
    fun myPage(session: HttpSession, model: Model): String {
        val userId = session.getAttribute("userId") as? Long
            ?: return "redirect:/login"
        val user = userService.findById(userId)
        val bookings = bookingRepository.findByUserIdOrderByStartDateDesc(userId)
        model.addAttribute("userName", user.name)
        model.addAttribute("bookings", bookings)
        return "my"
    }

    /** 예약 불가 날짜 조회 API */
    @GetMapping("/api/bookings/unavailable")
    @ResponseBody
    fun unavailableDates(@RequestParam from: LocalDate, @RequestParam to: LocalDate): List<String> {
        val bookings = bookingRepository.findByStartDateBetween(from, to)
        // 예약된 모든 날짜를 flat하게 반환
        return bookings.flatMap { b ->
            generateSequence(b.startDate) { it.plusDays(1) }
                .takeWhile { !it.isAfter(b.endDate) }
                .map { it.toString() }
                .toList()
        }.distinct()
    }

    /** 예약 생성 API */
    @PostMapping("/api/bookings")
    @ResponseBody
    fun createBooking(@RequestBody body: BookingRequest): ResponseEntity<Any> {
        val start = LocalDate.parse(body.startDate)
        val end = LocalDate.parse(body.endDate)

        // 중복 체크
        val overlapping = bookingRepository.findOverlapping(start, end)
        if (overlapping.isNotEmpty()) {
            return ResponseEntity.badRequest().body(mapOf("error" to "해당 기간에 이미 예약이 있습니다."))
        }

        val user = userService.findOrCreate(body.phone, body.customerName)
        bookingRepository.save(
            Booking(
                userId = user.id,
                tourName = body.tourName,
                startDate = start,
                endDate = end,
                groupSize = body.groupSize,
                phone = body.phone,
                customerName = body.customerName
            )
        )
        return ResponseEntity.ok(mapOf("message" to "예약 완료"))
    }
}

class BookingRequest {
    var customerName: String = ""
    var phone: String = ""
    var startDate: String = ""
    var endDate: String = ""
    var groupSize: Int = 2
    var tourName: String = ""
}
