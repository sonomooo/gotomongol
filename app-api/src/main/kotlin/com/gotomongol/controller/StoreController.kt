package com.gotomongol.controller

import com.gotomongol.application.AuthApplication
import com.gotomongol.application.QuoteApplication
import com.gotomongol.application.TripApplication
import com.gotomongol.tour.domain.AccommodationType
import com.gotomongol.user.dto.UserResponse
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Controller
class StoreController(
    private val quoteApp: QuoteApplication,
    private val tripApp: TripApplication,
    private val authApp: AuthApplication
) {

    // ─── 메인/투어 ───

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("tours", tripApp.findActiveTours())
        return "index"
    }

    @GetMapping("/tours/{id}")
    fun tourDetail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("tour", tripApp.findTourById(id))
        return "tour-detail"
    }

    // ─── 견적 요청 ───

    @GetMapping("/custom")
    fun customForm(): String = "custom"

    @PostMapping("/custom")
    fun submitQuote(
        @RequestParam customerName: String,
        @RequestParam phone: String,
        @RequestParam(required = false) email: String?,
        @RequestParam days: Int,
        @RequestParam groupSize: Int,
        @RequestParam(required = false) preferredDate: LocalDate?,
        @RequestParam(required = false) spots: List<String>?,
        @RequestParam(required = false) activities: List<String>?,
        @RequestParam accommodationType: AccommodationType,
        @RequestParam(required = false) memo: String?
    ): String {
        quoteApp.submit(customerName, phone, email, days, groupSize, preferredDate,
            spots ?: emptyList(), activities ?: emptyList(), accommodationType, memo)
        return "redirect:/custom/complete"
    }

    @GetMapping("/custom/complete")
    fun complete(): String = "complete"

    // ─── 로그인/인증 ───

    @GetMapping("/login")
    fun loginPage(): String = "login"

    @PostMapping("/api/auth/send-code")
    @ResponseBody
    fun sendCode(@RequestBody body: Map<String, String>): ResponseEntity<Map<String, String>> {
        authApp.sendCode(body["phone"]!!)
        return ResponseEntity.ok(mapOf("message" to "인증코드가 발송되었습니다."))
    }

    @PostMapping("/api/auth/verify")
    @ResponseBody
    fun verify(@RequestBody body: Map<String, String>, session: HttpSession): ResponseEntity<UserResponse> {
        val phone = body["phone"]!!
        val code = body["code"]!!
        val name = body["name"] ?: ""
        if (!authApp.verify(phone, code)) return ResponseEntity.badRequest().build()
        val user = authApp.loginOrRegister(phone, name)
        session.setAttribute("userId", user.id)
        return ResponseEntity.ok(UserResponse(user.id, user.name, user.phone, user.email, user.role.name))
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody
    fun logout(session: HttpSession): ResponseEntity<Map<String, String>> {
        session.invalidate()
        return ResponseEntity.ok(mapOf("message" to "로그아웃 되었습니다."))
    }

    @GetMapping("/api/auth/me")
    @ResponseBody
    fun me(session: HttpSession): ResponseEntity<UserResponse> {
        val userId = session.getAttribute("userId") as? Long ?: return ResponseEntity.status(401).build()
        val user = authApp.findUserById(userId)
        return ResponseEntity.ok(UserResponse(user.id, user.name, user.phone, user.email, user.role.name))
    }

    // ─── 예약 (캘린더) ───

    @GetMapping("/booking")
    fun bookingPage(): String = "booking"

    @GetMapping("/booking/complete")
    fun bookingComplete(): String = "booking-complete"

    @GetMapping("/api/bookings/unavailable")
    @ResponseBody
    fun unavailableDates(@RequestParam from: LocalDate, @RequestParam to: LocalDate): List<String> =
        tripApp.getUnavailableDates(from, to)

    @PostMapping("/api/bookings")
    @ResponseBody
    fun createBooking(@RequestBody body: Map<String, Any>): ResponseEntity<Any> {
        return try {
            tripApp.createBooking(
                customerName = body["customerName"] as String,
                phone = body["phone"] as String,
                tourName = body["tourName"] as String,
                startDate = LocalDate.parse(body["startDate"] as String),
                endDate = LocalDate.parse(body["endDate"] as String),
                groupSize = (body["groupSize"] as Number).toInt()
            )
            ResponseEntity.ok(mapOf("message" to "예약 완료"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    // ─── 내 여행 ───

    @GetMapping("/my/trips")
    fun myTrips(session: HttpSession, model: Model): String {
        val userId = session.getAttribute("userId") as? Long ?: return "redirect:/login"
        model.addAttribute("trips", tripApp.findTripsByUser(userId))
        model.addAttribute("userName", authApp.findUserById(userId).name)
        return "my-trips"
    }

    @GetMapping("/my/trips/{id}")
    fun tripDetail(@PathVariable id: Long, session: HttpSession, model: Model): String {
        val userId = session.getAttribute("userId") as? Long ?: return "redirect:/login"
        val trip = tripApp.findTripById(id)
        if (trip.userId != userId) return "redirect:/my/trips"
        model.addAttribute("trip", trip)
        return "trip-detail"
    }

    @GetMapping("/my/trips/{id}/calendar")
    @ResponseBody
    fun downloadIcs(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val trip = tripApp.findTripById(id)
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
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=trip-${trip.id}.ics")
            .header("Content-Type", "text/calendar")
            .body(ics.toByteArray())
    }
}
