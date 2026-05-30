package com.gotomongol.config

import com.gotomongol.tour.domain.ConfirmedTrip
import com.gotomongol.tour.domain.Tour
import com.gotomongol.tour.repository.ConfirmedTripRepository
import com.gotomongol.tour.repository.TourRepository
import com.gotomongol.user.domain.User
import com.gotomongol.user.domain.UserRole
import com.gotomongol.user.repository.UserRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DataInitializer(
    private val tourRepository: TourRepository,
    private val userRepository: UserRepository,
    private val confirmedTripRepository: ConfirmedTripRepository
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (tourRepository.count() == 0L) {
            tourRepository.saveAll(
                listOf(
                    Tour(name = "초원 힐링 코스", days = 4, description = "테를지 국립공원과 미니사막에서 즐기는 힐링 여행", minPrice = 800000, maxPrice = 1000000, spots = mutableListOf("테를지 국립공원", "미니사막"), activities = mutableListOf("승마", "게르 숙박")),
                    Tour(name = "고비사막 어드벤처", days = 6, description = "고비사막과 오르혼 폭포를 탐험하는 모험 코스", minPrice = 1300000, maxPrice = 1600000, spots = mutableListOf("고비사막", "오르혼 폭포"), activities = mutableListOf("낙타", "모래언덕 트레킹")),
                    Tour(name = "홉스골 프리미엄", days = 7, description = "몽골의 바이칼, 홉스골 호수와 온천을 즐기는 프리미엄 코스", minPrice = 1500000, maxPrice = 1800000, spots = mutableListOf("홉스골 호수", "쳉헤르 온천", "초원"), activities = mutableListOf("보트", "온천", "승마")),
                    Tour(name = "몽골 완전정복", days = 10, description = "고비부터 홉스골까지, 몽골의 모든 것을 경험하는 풀코스", minPrice = 2500000, maxPrice = 3000000, spots = mutableListOf("고비사막", "오르혼 폭포", "홉스골 호수", "테를지 국립공원"), activities = mutableListOf("승마", "낙타", "보트", "게르 숙박", "별보기"))
                )
            )
        }

        // 마스터 계정
        if (!userRepository.existsByPhone("01039941376")) {
            val master = userRepository.save(
                User(name = "유석진", phone = "01039941376", role = UserRole.ADMIN)
            )

            // 예시 확정 여행
            confirmedTripRepository.save(
                ConfirmedTrip(
                    userId = master.id,
                    tourName = "홉스골 호수 승마 투어",
                    startDate = LocalDate.of(2026, 7, 15),
                    endDate = LocalDate.of(2026, 7, 21),
                    groupSize = 2,
                    spots = "홉스골 호수",
                    dailySchedule = """1일차: 울란바토르 도착 → 시내 관광 → 숙소 체크인
2일차: 울란바토르 → 홉스골 이동 (국내선 항공) → 게르 캠프 도착
3일차: 홉스골 호수 승마 트레킹 (3시간) → 호수 산책 → 별 관측
4일차: 홉스골 보트 투어 → 유목민 가정 방문 → 전통 음식 체험
5일차: 승마로 주변 초원 탐방 → 낚시 체험 → 게르 캠프 자유시간
6일차: 홉스골 → 울란바토르 복귀 → 자유 쇼핑
7일차: 울란바토르 출국""",
                    meetingInfo = "울란바토르 칭기즈칸 국제공항 1층 로비, 오전 10시\n가이드가 '고투몽골' 팻말 들고 대기합니다.",
                    guideNote = "승마 초보, 게르 숙박 희망, 홉스골 호수 중심"
                )
            )
        }
    }
}
