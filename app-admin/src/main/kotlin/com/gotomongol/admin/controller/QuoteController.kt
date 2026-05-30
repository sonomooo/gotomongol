package com.gotomongol.admin.controller

import com.gotomongol.tour.domain.QuoteStatus
import com.gotomongol.tour.repository.QuoteRequestRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/quotes")
class QuoteController(private val quoteRequestRepository: QuoteRequestRepository) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("quotes", quoteRequestRepository.findAll())
        return "quotes"
    }

    @PostMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestParam status: QuoteStatus): String {
        val quote = quoteRequestRepository.findById(id).orElseThrow()
        quote.status = status
        quoteRequestRepository.save(quote)
        return "redirect:/quotes"
    }
}
