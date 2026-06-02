package com.gotomongol.controller

import com.gotomongol.application.QuoteApplication
import com.gotomongol.application.TripApplication
import com.gotomongol.application.dto.TripRegisterCommand
import com.gotomongol.domain.port.SiteConfigPort
import com.gotomongol.domain.port.TourPort
import com.gotomongol.domain.port.UserPort
import com.gotomongol.domain.response.ServiceResponse
import com.gotomongol.domain.tour.QuoteStatus
import com.gotomongol.domain.tour.SiteConfig
import com.gotomongol.domain.tour.Tour
import com.gotomongol.domain.user.UserRole
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
    private val tourPort: TourPort,
    private val siteConfigPort: SiteConfigPort,
    private val userPort: UserPort,
    @Value("\${upload.path:./uploads}") private val uploadPath: String
) {

    // ─── 대시보드 ───

    @GetMapping
    fun dashboard(session: HttpSession, model: Model): String {
        model.addAttribute("quoteCount", quoteApp.findAll().size)
        model.addAttribute("tripCount", tripApp.findAllTrips().size)
        model.addAttribute("userCount", userPort.count())
        model.addAttribute("tourCount", tourPort.count())
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
        model.addAttribute("tours", tourPort.findAll())
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
        val imageUrl = if (image != null && !image.isEmpty) saveFile(image) else null
        tourPort.save(Tour(
            name = name, days = days, description = description,
            minPrice = minPrice, maxPrice = maxPrice,
            spots = spots?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
            activities = activities?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
            imageUrl = imageUrl
        ))
        return "redirect:/admin/tours"
    }

    @GetMapping("/tours/{id}")
    fun editTour(@PathVariable id: Long, model: Model): String {
        val tour = tourPort.findById(id) ?: throw IllegalArgumentException("투어를 찾을 수 없습니다.")
        val allTours = tourPort.findAll()
        val allSpots = allTours.flatMap { it.spots }.distinct().sorted()
        val allActivities = allTours.flatMap { it.activities }.distinct().sorted()
        model.addAttribute("tour", tour)
        model.addAttribute("allSpots", allSpots)
        model.addAttribute("allActivities", allActivities)
        return "admin/tour-edit"
    }

    @PostMapping("/tours/{id}")
    fun updateTour(
        @PathVariable id: Long, @RequestParam name: String, @RequestParam days: Int,
        @RequestParam description: String, @RequestParam minPrice: Int,
        @RequestParam maxPrice: Int, @RequestParam(required = false) spots: List<String>?,
        @RequestParam(required = false) activities: List<String>?,
        @RequestParam(required = false) detailContent: String?,
        @RequestParam(required = false) image: MultipartFile?,
        @RequestParam(required = false) images: List<MultipartFile>?
    ): String {
        val existing = tourPort.findById(id) ?: throw IllegalArgumentException("투어를 찾을 수 없습니다.")
        val newImageUrl = if (image != null && !image.isEmpty) saveFile(image) else existing.imageUrl
        val newImageUrls = existing.imageUrls.toMutableList()
        images?.filter { !it.isEmpty }?.forEach { newImageUrls.add(saveFile(it)) }

        val allSpots = spots?.flatMap { it.split(",") }?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
        val allActivities = activities?.flatMap { it.split(",") }?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

        tourPort.save(existing.copy(
            name = name, days = days, description = description,
            minPrice = minPrice, maxPrice = maxPrice,
            spots = allSpots, activities = allActivities,
            detailContent = detailContent, imageUrl = newImageUrl, imageUrls = newImageUrls
        ))
        return "redirect:/admin/tours"
    }

    @PostMapping("/tours/{id}/toggle")
    fun toggleTour(@PathVariable id: Long): String {
        val tour = tourPort.findById(id) ?: throw IllegalArgumentException("투어를 찾을 수 없습니다.")
        tourPort.save(tour.copy(active = !tour.active))
        return "redirect:/admin/tours"
    }

    // ─── 사이트 설정 ───

    @GetMapping("/site")
    fun siteSettings(model: Model): String {
        val configs = siteConfigPort.findAll().associateBy { it.configKey }
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
        if (heroImage != null && !heroImage.isEmpty) saveConfig("heroImage", saveFile(heroImage))
        slogan?.let { saveConfig("slogan", it) }
        subText?.let { saveConfig("subText", it) }
        aboutText?.let { saveConfig("aboutText", it) }
        kakaoLink?.let { saveConfig("kakaoLink", it) }
        return "redirect:/admin/site"
    }

    // ─── 가격 설정 ───

    @GetMapping("/pricing")
    fun pricingPage(model: Model): String {
        model.addAttribute("configs", quoteApp.getAllPriceConfigs())
        return "admin/pricing"
    }

    @PostMapping("/pricing")
    @ResponseBody
    fun savePricing(@RequestBody body: Map<String, Any>): ServiceResponse<Nothing> {
        quoteApp.savePriceConfig(com.gotomongol.domain.tour.PriceConfig(
            id = (body["id"] as? Number)?.toLong() ?: 0,
            category = com.gotomongol.domain.tour.PriceCategory.valueOf(body["category"] as String),
            itemName = body["itemName"] as String,
            pricePerUnit = (body["pricePerUnit"] as Number).toInt(),
            unit = body["unit"] as? String ?: "1일"
        ))
        return ServiceResponse.success()
    }

    @PostMapping("/pricing/{id}/delete")
    fun deletePricing(@PathVariable id: Long): String {
        quoteApp.deletePriceConfig(id)
        return "redirect:/admin/pricing"
    }

    private fun saveConfig(key: String, value: String) {
        val existing = siteConfigPort.findByKey(key)
        if (existing != null) {
            siteConfigPort.save(existing.copy(configValue = value))
        } else {
            siteConfigPort.save(SiteConfig(configKey = key, configValue = value))
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
