package com.gotomongol.controller

import com.gotomongol.application.AuthApplication
import com.gotomongol.application.QuoteApplication
import com.gotomongol.application.TripApplication
import com.gotomongol.application.dto.BookingCommand
import com.gotomongol.application.dto.QuoteSubmitCommand
import com.gotomongol.domain.response.ServiceErrorType
import com.gotomongol.domain.response.ServiceResponse
import com.gotomongol.user.dto.UserResponse
import jakarta.servlet.http.HttpSession
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
class StoreController(
    private val quoteApp: QuoteApplication,
    private val tripApp: TripApplication,
    private val authApp: AuthApplication
) {

    // ─── 페이지 라우팅 ───

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

    @GetMapping("/custom")
    fun customForm(): String = "custom"

    @GetMapping("/custom/complete")
    fun customComplete(): String = "complete"

    @GetMapping("/login")
    fun loginPage(): String = "login"

    @GetMapping("/booking")
    fun bookingPage(): String = "booking"

    @GetMapping("/booking/complete")
    fun bookingComplete(): String = "booking-complete"

    @GetMapping("/my/trips")
    fun myTrips(session: HttpSession, model: Model): String {
        val userId = session.getAttribute("userId") as? Long ?: return "redirect:/login"
        model.addAttribute("trips", tripApp.findTripsByUser(userId))
        model.addAttribute("userName", authApp.findUserById(userId).name)
        return "my-trips"
    }

    @GetMapping("/my/trips/{id}")
    fun myTripDetail(@PathVariable id: Long, session: HttpSession, model: Model): String {
        val userId = session.getAttribute("userId") as? Long ?: return "redirect:/login"
        val trip = tripApp.findTripById(id)
        if (trip.userId != userId) return "redirect:/my/trips"
        model.addAttribute("trip", trip)
        return "trip-detail"
    }

    // ─── API: 견적 ───

    @PostMapping("/custom")
    fun submitQuote(
        @RequestParam customerName: String, @RequestParam phone: String,
        @RequestParam(required = false) email: String?, @RequestParam days: Int,
        @RequestParam groupSize: Int, @RequestParam(required = false) preferredDate: LocalDate?,
        @RequestParam(required = false) spots: List<String>?,
        @RequestParam(required = false) activities: List<String>?,
        @RequestParam accommodationType: String, @RequestParam(required = false) memo: String?
    ): String {
        quoteApp.submit(QuoteSubmitCommand(customerName, phone, email, days, groupSize,
            preferredDate, spots ?: emptyList(), activities ?: emptyList(), accommodationType, memo))
        return "redirect:/custom/complete"
    }

    // ─── API: 인증 ───

    @PostMapping("/api/auth/send-code")
    @ResponseBody
    fun sendCode(@RequestBody body: Map<String, String>): ServiceResponse<Nothing> {
        authApp.sendCode(body["phone"]!!)
        return ServiceResponse.success()
    }

    @PostMapping("/api/auth/verify")
    @ResponseBody
    fun verify(@RequestBody body: Map<String, String>, session: HttpSession): ServiceResponse<UserResponse> {
        val phone = body["phone"]!!
        val code = body["code"]!!
        val name = body["name"] ?: ""
        if (!authApp.verify(phone, code)) return ServiceResponse.error(ServiceErrorType.VERIFICATION_FAILED)
        val user = authApp.loginOrRegister(phone, name)
        session.setAttribute("userId", user.id)
        return ServiceResponse.success(UserResponse(user.id, user.name, user.phone, user.email, user.role.name))
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody
    fun logout(session: HttpSession): ServiceResponse<Nothing> {
        session.invalidate()
        return ServiceResponse.success()
    }

    @GetMapping("/api/auth/me")
    @ResponseBody
    fun me(session: HttpSession): ServiceResponse<UserResponse> {
        val userId = session.getAttribute("userId") as? Long
            ?: return ServiceResponse.error(ServiceErrorType.UNAUTHORIZED)
        val user = authApp.findUserById(userId)
        return ServiceResponse.success(UserResponse(user.id, user.name, user.phone, user.email, user.role.name))
    }

    // ─── API: 예약 ───

    @GetMapping("/api/bookings/unavailable")
    @ResponseBody
    fun unavailableDates(@RequestParam from: LocalDate, @RequestParam to: LocalDate): ServiceResponse<List<String>> =
        ServiceResponse.success(tripApp.getUnavailableDates(from, to))

    @PostMapping("/api/bookings")
    @ResponseBody
    fun createBooking(@RequestBody body: Map<String, Any>): ServiceResponse<Nothing> {
        return try {
            tripApp.createBooking(BookingCommand(
                customerName = body["customerName"] as String,
                phone = body["phone"] as String,
                tourName = body["tourName"] as String,
                startDate = LocalDate.parse(body["startDate"] as String),
                endDate = LocalDate.parse(body["endDate"] as String),
                groupSize = (body["groupSize"] as Number).toInt()
            ))
            ServiceResponse.success()
        } catch (e: IllegalArgumentException) {
            ServiceResponse.error(ServiceErrorType.BOOKING_CONFLICT, e.message)
        }
    }

    // ─── API: 캘린더 ───

    @GetMapping("/my/trips/{id}/calendar")
    @ResponseBody
    fun downloadCalendar(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val file = tripApp.generateCalendar(id)
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=${file.filename}")
            .header("Content-Type", "text/calendar")
            .body(file.content)
    }
}
