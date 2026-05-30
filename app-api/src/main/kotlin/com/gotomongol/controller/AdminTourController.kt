package com.gotomongol.controller

import com.gotomongol.tour.domain.Tour
import com.gotomongol.tour.repository.TourRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Controller
@RequestMapping("/admin/tours")
class AdminTourController(
    private val tourRepository: TourRepository,
    @Value("\${upload.path:./uploads}") private val uploadPath: String
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("tours", tourRepository.findAll())
        return "admin/tours"
    }

    @PostMapping("/new")
    fun create(
        @RequestParam name: String,
        @RequestParam days: Int,
        @RequestParam description: String,
        @RequestParam minPrice: Int,
        @RequestParam maxPrice: Int,
        @RequestParam(required = false) spots: String?,
        @RequestParam(required = false) activities: String?,
        @RequestParam(required = false) image: MultipartFile?
    ): String {
        val tour = Tour(
            name = name, days = days, description = description,
            minPrice = minPrice, maxPrice = maxPrice,
            spots = spots?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf(),
            activities = activities?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toMutableList() ?: mutableListOf()
        )
        if (image != null && !image.isEmpty) tour.imageUrl = saveFile(image)
        tourRepository.save(tour)
        return "redirect:/admin/tours"
    }

    @GetMapping("/{id}")
    fun edit(@PathVariable id: Long, model: Model): String {
        model.addAttribute("tour", tourRepository.findById(id).orElseThrow())
        return "admin/tour-edit"
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestParam days: Int,
        @RequestParam description: String,
        @RequestParam minPrice: Int,
        @RequestParam maxPrice: Int,
        @RequestParam spots: String,
        @RequestParam activities: String,
        @RequestParam(required = false) detailContent: String?,
        @RequestParam(required = false) image: MultipartFile?
    ): String {
        val tour = tourRepository.findById(id).orElseThrow()
        tour.name = name; tour.days = days; tour.description = description
        tour.minPrice = minPrice; tour.maxPrice = maxPrice
        tour.spots = spots.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
        tour.activities = activities.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
        tour.detailContent = detailContent
        if (image != null && !image.isEmpty) tour.imageUrl = saveFile(image)
        tourRepository.save(tour)
        return "redirect:/admin/tours"
    }

    @PostMapping("/{id}/toggle")
    fun toggle(@PathVariable id: Long): String {
        val tour = tourRepository.findById(id).orElseThrow()
        tour.active = !tour.active
        tourRepository.save(tour)
        return "redirect:/admin/tours"
    }

    private fun saveFile(file: MultipartFile): String {
        val dir = Paths.get(uploadPath)
        if (!Files.exists(dir)) Files.createDirectories(dir)
        val filename = "${UUID.randomUUID()}_${file.originalFilename}"
        Files.copy(file.inputStream, dir.resolve(filename))
        return "/uploads/$filename"
    }
}
