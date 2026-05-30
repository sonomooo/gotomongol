package com.gotomongol.config

import com.gotomongol.tour.domain.Tour
import com.gotomongol.tour.repository.TourRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class DataInitializer(private val tourRepository: TourRepository) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (tourRepository.count() == 0L) {
            tourRepository.saveAll(
                listOf(
                    Tour(name = "초원 힐링 코스", days = 4, description = "테를지 국립공원과 미니사막에서 즐기는 힐링 여행", minPrice = 800000, maxPrice = 1000000, spots = listOf("테를지 국립공원", "미니사막"), activities = listOf("승마", "게르 숙박")),
                    Tour(name = "고비사막 어드벤처", days = 6, description = "고비사막과 오르혼 폭포를 탐험하는 모험 코스", minPrice = 1300000, maxPrice = 1600000, spots = listOf("고비사막", "오르혼 폭포"), activities = listOf("낙타", "모래언덕 트레킹")),
                    Tour(name = "홉스골 프리미엄", days = 7, description = "몽골의 바이칼, 홉스골 호수와 온천을 즐기는 프리미엄 코스", minPrice = 1500000, maxPrice = 1800000, spots = listOf("홉스골 호수", "쳉헤르 온천", "초원"), activities = listOf("보트", "온천", "승마")),
                    Tour(name = "몽골 완전정복", days = 10, description = "고비부터 홉스골까지, 몽골의 모든 것을 경험하는 풀코스", minPrice = 2500000, maxPrice = 3000000, spots = listOf("고비사막", "오르혼 폭포", "홉스골 호수", "테를지 국립공원"), activities = listOf("승마", "낙타", "보트", "게르 숙박", "별보기"))
                )
            )
        }
    }
}
