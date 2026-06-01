package com.gotomongol.config

import com.gotomongol.domain.port.ConfirmedTripPort
import com.gotomongol.domain.port.TourPort
import com.gotomongol.domain.port.UserPort
import com.gotomongol.domain.tour.ConfirmedTrip
import com.gotomongol.domain.tour.Tour
import com.gotomongol.domain.user.User
import com.gotomongol.domain.user.UserRole
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DataInitializer(
    private val tourPort: TourPort,
    private val userPort: UserPort,
    private val confirmedTripPort: ConfirmedTripPort
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (tourPort.count() == 0L) {
            listOf(
                Tour(name = "초원 힐링 코스", days = 4, description = "테를지 국립공원과 미니사막에서 즐기는 힐링 여행", minPrice = 800000, maxPrice = 1000000, spots = listOf("테를지 국립공원", "미니사막"), activities = listOf("승마", "게르 숙박")),
                Tour(name = "고비사막 어드벤처", days = 6, description = "고비사막과 오르혼 폭포를 탐험하는 모험 코스", minPrice = 1300000, maxPrice = 1600000, spots = listOf("고비사막", "오르혼 폭포"), activities = listOf("낙타", "모래언덕 트레킹")),
                Tour(name = "홉스골 프리미엄", days = 7, description = "몽골의 바이칼, 홉스골 호수와 온천을 즐기는 프리미엄 코스", minPrice = 1500000, maxPrice = 1800000, spots = listOf("홉스골 호수", "쳉헤르 온천", "초원"), activities = listOf("보트", "온천", "승마")),
                Tour(name = "몽골 완전정복", days = 10, description = "고비부터 홉스골까지, 몽골의 모든 것을 경험하는 풀코스", minPrice = 2500000, maxPrice = 3000000, spots = listOf("고비사막", "오르혼 폭포", "홉스골 호수", "테를지 국립공원"), activities = listOf("승마", "낙타", "보트", "게르 숙박", "별보기"))
            ).forEach { tourPort.save(it) }
        }

        if (!userPort.existsByPhone("01039941376")) {
            val master = userPort.save(User(name = "유석진", phone = "01039941376", role = UserRole.ADMIN))
            confirmedTripPort.save(ConfirmedTrip(
                userId = master.id, tourName = "홉스골 호수 승마 투어",
                startDate = LocalDate.of(2026, 7, 15), endDate = LocalDate.of(2026, 7, 21),
                groupSize = 2, spots = "홉스골 호수",
                dailySchedule = "1일차: 울란바토르 도착\n2일차: 홉스골 이동\n3일차: 승마 트레킹\n4일차: 보트 투어\n5일차: 초원 탐방\n6일차: 울란바토르 복귀\n7일차: 출국",
                meetingInfo = "울란바토르 공항 1층 로비, 오전 10시"
            ))
        }
    }
}
