package com.gotomongol.controller

import com.gotomongol.tour.domain.AccommodationType
import com.gotomongol.tour.domain.QuoteRequest
import com.gotomongol.tour.repository.QuoteRequestRepository
import com.gotomongol.tour.repository.TourRepository
import com.gotomongol.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Controller
class TourController(
    private val tourRepository: TourRepository,
    private val quoteRequestRepository: QuoteRequestRepository,
    private val userService: UserService
) {

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("tours", tourRepository.findByActiveTrue())
        return "index"
    }

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
        // 비회원이어도 자동으로 유저 생성/매칭
        userService.findOrCreate(phone, customerName)

        quoteRequestRepository.save(
            QuoteRequest(
                customerName = customerName,
                phone = phone,
                email = email,
                days = days,
                groupSize = groupSize,
                preferredDate = preferredDate,
                spots = spots ?: emptyList(),
                activities = activities ?: emptyList(),
                accommodationType = accommodationType,
                memo = memo
            )
        )
        return "redirect:/custom/complete"
    }

    @GetMapping("/custom/complete")
    fun complete(): String = "complete"

    @GetMapping("/tours/{id}")
    fun tourDetail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("tour", tourRepository.findById(id).orElseThrow())
        return "tour-detail"
    }
}
