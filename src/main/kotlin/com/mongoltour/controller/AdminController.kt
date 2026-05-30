package com.mongoltour.controller

import com.mongoltour.domain.QuoteStatus
import com.mongoltour.repository.QuoteRequestRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin")
class AdminController(
    private val quoteRequestRepository: QuoteRequestRepository
) {

    @GetMapping("/quotes")
    fun listQuotes(model: Model): String {
        model.addAttribute("quotes", quoteRequestRepository.findAll())
        return "admin/quotes"
    }

    @PostMapping("/quotes/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestParam status: QuoteStatus): String {
        val quote = quoteRequestRepository.findById(id).orElseThrow()
        quote.status = status
        quoteRequestRepository.save(quote)
        return "redirect:/admin/quotes"
    }
}
