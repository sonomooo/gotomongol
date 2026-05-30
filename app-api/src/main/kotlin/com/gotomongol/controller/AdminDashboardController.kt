package com.gotomongol.controller

import com.gotomongol.tour.repository.ConfirmedTripRepository
import com.gotomongol.tour.repository.QuoteRequestRepository
import com.gotomongol.tour.repository.TourRepository
import com.gotomongol.user.repository.UserRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin")
class AdminDashboardController(
    private val quoteRequestRepository: QuoteRequestRepository,
    private val confirmedTripRepository: ConfirmedTripRepository,
    private val userRepository: UserRepository,
    private val tourRepository: TourRepository
) {
    @GetMapping
    fun dashboard(model: Model): String {
        model.addAttribute("quoteCount", quoteRequestRepository.count())
        model.addAttribute("tripCount", confirmedTripRepository.count())
        model.addAttribute("userCount", userRepository.count())
        model.addAttribute("tourCount", tourRepository.count())
        return "admin/dashboard"
    }
}
