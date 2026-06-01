package com.gotomongol.controller

import com.gotomongol.application.QuoteApplication
import com.gotomongol.application.TripApplication
import com.gotomongol.application.dto.TripRegisterCommand
import com.gotomongol.domain.response.ServiceResponse
import com.gotomongol.tour.domain.QuoteStatus
import com.gotomongol.tour.domain.SiteConfig
import com.gotomongol.tour.domain.Tour
import com.gotomongol.tour.repository.SiteConfigRepository
import com.gotomongol.tour.repository.TourRepository
import com.gotomongol.user.repository.UserRepository
import jakarta.servlet.http.HttpSession
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.util.*

@Controller
@RequestMapping("/admin")
class AdminController(
    private val quoteApp: QuoteApplication,
    private val tripApp: TripApplication,
    private val tourRepository: TourRepository,
    private val siteConfigRepository: SiteConfigRepository,
    private val userRepository: UserRepository,
    @Value("\${upload.path:./uploads}") private val uploadPath: String
) {

    private fun requireAdmin(session: HttpSession) {
        val userId = session.getAttribute("userId") as? Long
            ?: throw IllegalStateException("LOGIN_REQUIRED")
        val user = userRepository.findById(userId).orElseThrow()
        if (user.role != com.gotomongol.user.domain.UserRole.ADMIN) {
            throw IllegalStateException("ADMIN_ONLY")
        }
    }

    // ─── 대시보드 ───

    @GetMapping
    fun dashboard(session: HttpSession, model: Model): String {
        // requireAdmin(session) // TODO: 운영 시 활성화
        model.addAttribute("quoteCount", quoteApp.findAll().size)
        model.addAttribute("tripCount", tripApp.findAllTrips().size)
        model.addAttribute("userCount", userRepository.count())
        model.addAttribute("tourCount", tourRepository.count())
        return "admin/dashboard"
    }

    // ─── 견적 관리 ───

    @GetMapping("/quotes")
    fun listQuotes(@RequestParam(required = false) status: QuoteStatus?, model: Model): String {
        val quotes = if (status != null) quoteApp.findByStatus(status) else quoteApp.findAll()
        model.addAttribute("quotes", quotes)
        model.addAttribute("currentStatus", status?.name ?: "ALL")
        return "admin/quotes"
    }

    @PostMapping("/quotes/{id}/status")
    fun updateQuoteStatus(@PathVariable id: Long, @RequestParam status: QuoteStatus): String {
        quoteApp.updateStatus(id, status)
        return "redirect:/admin/quotes"
    }

    // ─── 여행 관리 ───

    @GetMapping("/trips")
    fun listTrips(model: Model): String {
        model.addAttribute("trips", tripApp.findAllTrips())
        return "admin/trips"
    }

    @GetMapping("/trips/{id}")
    fun tripDetail(@PathVariable id: Long, model: Model): String {
        model.addAttribute("trip", tripApp.findTripById(id))
        return "admin/trip-detail"
    }

    @PostMapping("/trips")
    @ResponseBody
    fun createTrip(@RequestBody body: Map<String, Any?>): ServiceResponse<Map<String, Any>> {
        val cmd = TripRegisterCommand(
            phone = body["phone"] as? String,
            customerName = body["customerName"] as? String,
            quoteRequestId = (body["quoteRequestId"] as? Number)?.toLong(),
            tourName = body["tourName"] as String,
            startDate = LocalDate.parse(body["startDate"] as String),
            endDate = LocalDate.parse(body["endDate"] as String),
            groupSize = (body["groupSize"] as Number).toInt(),
            spots = body["spots"] as String,
            dailySchedule = body["dailySchedule"] as String,
            meetingInfo = body["meetingInfo"] as? String,
            guideNote = body["guideNote"] as? String
        )
        val result = tripApp.registerTrip(cmd)
        return ServiceResponse.success(mapOf("id" to result.id, "message" to result.message))
    }

    // ─── 투어 상품 관리 ───

    @GetMapping("/tours")
    fun listTours(model: Model): String {
        model.addAttribute("tours", tourRepository.findAll())
        return "admin/tours"
    }

    @PostMapping("/tours/new")
    fun createTour(
        @RequestParam name: String, @RequestParam days: Int,
        @RequestParam description: String, @RequestParam minPrice: Int,
        @RequestParam maxPrice: Int, @RequestParam(required = false) spots: String?,
        @RequestParam(required = false) activities: String?,
        @RequestParam(required = false) image: MultipartFile?
    ): String {
        val tour = Tour(name = name, days = days, description = description,
            minPrice = minPrice, maxPrice = maxPrice,
            spots = spots?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf(),
            activities = activities?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf())
        if (image != null && !image.isEmpty) tour.imageUrl = saveFile(image)
        tourRepository.save(tour)
        return "redirect:/admin/tours"
    }

    @GetMapping("/tours/{id}")
    fun editTour(@PathVariable id: Long, model: Model): String {
        model.addAttribute("tour", tourRepository.findById(id).orElseThrow())
        return "admin/tour-edit"
    }

    @PostMapping("/tours/{id}")
    fun updateTour(
        @PathVariable id: Long, @RequestParam name: String, @RequestParam days: Int,
        @RequestParam description: String, @RequestParam minPrice: Int,
        @RequestParam maxPrice: Int, @RequestParam spots: String,
        @RequestParam activities: String, @RequestParam(required = false) detailContent: String?,
        @RequestParam(required = false) image: MultipartFile?,
        @RequestParam(required = false) images: List<MultipartFile>?
    ): String {
        val tour = tourRepository.findById(id).orElseThrow()
        tour.name = name; tour.days = days; tour.description = description
        tour.minPrice = minPrice; tour.maxPrice = maxPrice
        tour.spots = spots.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
        tour.activities = activities.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
        tour.detailContent = detailContent
        if (image != null && !image.isEmpty) tour.imageUrl = saveFile(image)
        images?.filter { !it.isEmpty }?.forEach { tour.imageUrls.add(saveFile(it)) }
        tourRepository.save(tour)
        return "redirect:/admin/tours"
    }

    @PostMapping("/tours/{id}/toggle")
    fun toggleTour(@PathVariable id: Long): String {
        val tour = tourRepository.findById(id).orElseThrow()
        tour.active = !tour.active
        tourRepository.save(tour)
        return "redirect:/admin/tours"
    }

    // ─── 사이트 설정 ───

    @GetMapping("/site")
    fun siteSettings(model: Model): String {
        val configs = siteConfigRepository.findAll().associateBy { it.configKey }
        model.addAttribute("configs", configs)
        return "admin/site"
    }

    @PostMapping("/site")
    fun updateSiteSettings(
        @RequestParam heroImage: MultipartFile?,
        @RequestParam(required = false) slogan: String?,
        @RequestParam(required = false) subText: String?,
        @RequestParam(required = false) aboutText: String?,
        @RequestParam(required = false) kakaoLink: String?
    ): String {
        if (heroImage != null && !heroImage.isEmpty) {
            saveConfig("heroImage", saveFile(heroImage))
        }
        slogan?.let { saveConfig("slogan", it) }
        subText?.let { saveConfig("subText", it) }
        aboutText?.let { saveConfig("aboutText", it) }
        kakaoLink?.let { saveConfig("kakaoLink", it) }
        return "redirect:/admin/site"
    }

    private fun saveConfig(key: String, value: String) {
        val config = siteConfigRepository.findByConfigKey(key)
        if (config != null) {
            config.configValue = value
            siteConfigRepository.save(config)
        } else {
            siteConfigRepository.save(SiteConfig(configKey = key, configValue = value))
        }
    }

    private fun saveFile(file: MultipartFile): String {
        val dir = Paths.get(uploadPath)
        if (!Files.exists(dir)) Files.createDirectories(dir)
        val filename = "${UUID.randomUUID()}_${file.originalFilename}"
        Files.copy(file.inputStream, dir.resolve(filename))
        return "/uploads/$filename"
    }
}
