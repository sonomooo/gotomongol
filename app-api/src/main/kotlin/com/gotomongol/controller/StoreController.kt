package com.gotomongol.controller

import com.gotomongol.application.AuthApplication
import com.gotomongol.application.QuoteApplication
import com.gotomongol.application.ReviewApplication
import com.gotomongol.application.TripApplication
import com.gotomongol.application.dto.BookingCommand
import com.gotomongol.application.dto.QuoteSubmitCommand
import com.gotomongol.domain.port.ActivityItemPort
import com.gotomongol.domain.port.BoardPostPort
import com.gotomongol.domain.port.FoodItemPort
import com.gotomongol.domain.port.SiteConfigPort
import com.gotomongol.domain.port.SpotItemPort
import com.gotomongol.domain.response.ServiceErrorType
import com.gotomongol.domain.response.ServiceResponse
import com.gotomongol.domain.user.User
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
    private val authApp: AuthApplication,
    private val reviewApp: ReviewApplication,
    private val siteConfigPort: SiteConfigPort,
    private val spotItemPort: SpotItemPort,
    private val activityItemPort: ActivityItemPort,
    private val foodItemPort: FoodItemPort,
    private val boardPostPort: BoardPostPort
) {

    // ─── 페이지 라우팅 ───

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("tours", tripApp.findActiveTours())
        model.addAttribute("reviews", reviewApp.findAll().take(3))
        val configs = siteConfigPort.findAll().associateBy { it.configKey }
        model.addAttribute("site", configs.mapValues { it.value.configValue })
        return "index"
    }

    @GetMapping("/tours/{id}")
    fun tourDetail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("tour", tripApp.findTourById(id))
        return "tour-detail"
    }

    @GetMapping("/custom")
    fun customForm(model: Model): String {
        model.addAttribute("allSpots", spotItemPort.findActive().map { it.name })
        model.addAttribute("allActivities", activityItemPort.findActive().map { it.name })
        model.addAttribute("allFoods", foodItemPort.findActive().map { it.name })
        return "custom"
    }

    @GetMapping("/custom/complete")
    fun customComplete(): String {
        return "complete"
    }

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }

    @GetMapping("/privacy")
    fun privacyPage(): String {
        return "privacy"
    }

    @GetMapping("/terms")
    fun termsPage(): String {
        return "terms"
    }

    @GetMapping("/booking")
    fun bookingPage(): String {
        return "booking"
    }

    @GetMapping("/booking/complete")
    fun bookingComplete(): String {
        return "booking-complete"
    }

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
        model.addAttribute("quote", tripApp.findQuoteByTripId(id))
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
    fun sendCode(@RequestBody body: Map<String, Any>): ServiceResponse<Nothing> {
        authApp.sendCode(body["phone"] as String)
        return ServiceResponse.success()
    }

    @PostMapping("/api/auth/verify")
    @ResponseBody
    fun verify(@RequestBody body: Map<String, Any>, session: HttpSession): ServiceResponse<User> {
        val phone = body["phone"] as String
        val code = body["code"] as String
        val name = body["name"] as? String ?: ""
        val termsAgreed = body["termsAgreed"] as? Boolean ?: false
        val privacyAgreed = body["privacyAgreed"] as? Boolean ?: false
        val marketingAgreed = body["marketingAgreed"] as? Boolean ?: false
        if (!authApp.verify(phone, code)) return ServiceResponse.error(ServiceErrorType.VERIFICATION_FAILED)
        val user = authApp.loginOrRegister(phone, name, termsAgreed, privacyAgreed, marketingAgreed)
        session.setAttribute("userId", user.id)
        return ServiceResponse.success(user)
    }

    @PostMapping("/api/auth/logout")
    @ResponseBody
    fun logout(session: HttpSession): ServiceResponse<Nothing> {
        session.invalidate()
        return ServiceResponse.success()
    }

    @GetMapping("/api/auth/me")
    @ResponseBody
    fun me(session: HttpSession): ServiceResponse<User> {
        val userId = session.getAttribute("userId") as? Long
            ?: return ServiceResponse.error(ServiceErrorType.UNAUTHORIZED)
        val user = authApp.findUserById(userId)
        return ServiceResponse.success(user)
    }

    // ─── API: 예약 ───

    @GetMapping("/api/bookings/unavailable")
    @ResponseBody
    fun unavailableDates(@RequestParam from: LocalDate, @RequestParam to: LocalDate): ServiceResponse<List<String>> {
        return ServiceResponse.success(tripApp.getUnavailableDates(from, to))
    }

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

    // ─── 게시판 ───

    @GetMapping("/board")
    fun board(@RequestParam(required = false, defaultValue = "NOTICE") category: String, model: Model): String {
        model.addAttribute("posts", boardPostPort.findByCategory(category))
        model.addAttribute("currentCategory", category)
        return "board"
    }

    @GetMapping("/board/{id}")
    fun boardDetail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("post", boardPostPort.findById(id))
        return "board-detail"
    }

    // ─── 후기 ───

    @GetMapping("/reviews")
    fun reviewList(model: Model): String {
        model.addAttribute("reviews", reviewApp.findAll())
        return "reviews"
    }

    @GetMapping("/reviews/write")
    fun reviewForm(session: HttpSession): String {
        val userId = session.getAttribute("userId") as? Long ?: return "redirect:/login"
        return "review-write"
    }

    @PostMapping("/reviews")
    fun submitReview(
        session: HttpSession,
        @RequestParam tourName: String,
        @RequestParam title: String,
        @RequestParam content: String,
        @RequestParam rating: Int,
        @RequestParam(required = false) imageUrls: List<String>?
    ): String {
        val userId = session.getAttribute("userId") as? Long ?: return "redirect:/login"
        reviewApp.create(userId, tourName, title, content, rating, imageUrls ?: emptyList())
        return "redirect:/reviews"
    }

    @PostMapping("/reviews/{id}/delete")
    fun deleteReview(@PathVariable id: Long, session: HttpSession): String {
        val userId = session.getAttribute("userId") as? Long ?: return "redirect:/login"
        reviewApp.delete(id, userId)
        return "redirect:/reviews"
    }

    // ─── API: 견적 계산 ───

    @PostMapping("/api/estimate")
    @ResponseBody
    fun estimate(@RequestBody body: Map<String, Any>): ServiceResponse<Map<String, Any>> {
        val result = quoteApp.estimate(
            days = (body["days"] as Number).toInt(),
            groupSize = (body["groupSize"] as Number).toInt(),
            spots = (body["spots"] as? List<*>)?.map { it.toString() } ?: emptyList(),
            activities = (body["activities"] as? List<*>)?.map { it.toString() } ?: emptyList(),
            foods = (body["foods"] as? List<*>)?.map { it.toString() } ?: emptyList(),
            accommodation = body["accommodation"] as? String ?: "CAMP"
        )
        return ServiceResponse.success(result)
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
