package com.gotomongol

import com.gotomongol.tour.repository.ConfirmedTripRepository
import com.gotomongol.tour.repository.QuoteRequestRepository
import com.gotomongol.user.repository.UserRepository
import com.gotomongol.user.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.client.RestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TourFlowIntegrationTest {

    @LocalServerPort
    var port: Int = 0

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var quoteRequestRepository: QuoteRequestRepository
    @Autowired lateinit var confirmedTripRepository: ConfirmedTripRepository
    @Autowired lateinit var userService: UserService

    val client by lazy { RestClient.create("http://localhost:$port") }

    @Test
    @Order(1)
    fun `비회원 견적 요청 - 자동 유저 생성`() {
        val res = client.post().uri("/custom")
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .body("customerName=%EA%B9%80%EB%B9%84%ED%9A%8C%EC%9B%90&phone=010-1111-1111&days=5&groupSize=2&accommodationType=CAMP")
            .retrieve()
            .toBodilessEntity()

        assertThat(res.statusCode.value()).isIn(302, 200)

        val user = userRepository.findByPhone("010-1111-1111")
        assertThat(user).isNotNull
        assertThat(user!!.name).isEqualTo("김비회원")

        assertThat(quoteRequestRepository.count()).isEqualTo(1)
    }

    @Test
    @Order(2)
    fun `회원 인증코드 발송 후 견적 요청`() {
        val sendRes = client.post().uri("/api/auth/send-code")
            .header("Content-Type", "application/json")
            .body("""{"phone":"010-2222-2222"}""")
            .retrieve()
            .body(Map::class.java)

        assertThat(sendRes!!["message"]).isEqualTo("인증코드가 발송되었습니다.")

        client.post().uri("/custom")
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .body("customerName=%EB%B0%95%ED%9A%8C%EC%9B%90&phone=010-2222-2222&days=7&groupSize=4&spots=%ED%99%89%EC%8A%A4%EA%B3%A8+%ED%98%B8%EC%88%98&activities=%EC%8A%B9%EB%A7%88&accommodationType=PREMIUM_CAMP&memo=test")
            .retrieve()
            .toBodilessEntity()

        val user = userRepository.findByPhone("010-2222-2222")
        assertThat(user).isNotNull
        assertThat(user!!.name).isEqualTo("박회원")
        assertThat(quoteRequestRepository.count()).isEqualTo(2)
    }

    @Test
    @Order(3)
    fun `어드민 - 견적 목록 확인`() {
        val res = client.get().uri("/admin/quotes")
            .retrieve()
            .body(String::class.java)

        assertThat(res).contains("010-1111-1111")
        assertThat(res).contains("010-2222-2222")
    }

    @Test
    @Order(4)
    fun `어드민 - 확정 여행 등록 (스케줄 전달)`() {
        val userId = userRepository.findByPhone("010-2222-2222")!!.id

        val res = client.post().uri("/admin/trips")
            .header("Content-Type", "application/json; charset=UTF-8")
            .body("""
                {
                    "userId": $userId,
                    "tourName": "Hobsgol Premium",
                    "startDate": "2026-07-10",
                    "endDate": "2026-07-16",
                    "groupSize": 4,
                    "spots": "Hobsgol, Oncheon",
                    "dailySchedule": "Day1: UB to Desert\nDay2: Hot spring\nDay3: Hobsgol",
                    "meetingInfo": "Airport 1F, 9AM",
                    "guideNote": "beginner rider"
                }
            """)
            .retrieve()
            .body(Map::class.java)

        assertThat(res!!["message"].toString()).contains("여행 등록 완료")
        assertThat(confirmedTripRepository.count()).isEqualTo(1)
    }

    @Test
    @Order(5)
    fun `회원 - 확정 여행 캘린더 다운로드`() {
        val tripId = confirmedTripRepository.findAll().first().id

        val ics = client.get().uri("/my/trips/$tripId/calendar")
            .retrieve()
            .body(String::class.java)

        assertThat(ics).contains("BEGIN:VCALENDAR")
        assertThat(ics).contains("Hobsgol Premium")
        assertThat(ics).contains("DTSTART;VALUE=DATE:20260710")
    }
}
