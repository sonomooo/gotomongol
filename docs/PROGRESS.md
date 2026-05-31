# 고투몽골 (gotomongol.com) - 개발 현황

## 프로젝트 구조

```
gotomongol/
├── core-domain/           ← BaseEntity, 이벤트 정의, ServiceResponse
├── core-notification/     ← 알림 이벤트 리스너 (SMS, 카톡 - TODO)
├── user-core/             ← User, VerificationCode, UserService, 이벤트 리스너
├── tour-core/             ← Tour, QuoteRequest, Booking, ConfirmedTrip + Repository
├── application/           ← 유스케이스 (QuoteApp, TripApp, AuthApp) + DTO
└── app-api/               ← 컨트롤러 2개 (StoreController, AdminController) + 설정
```

## 아키텍처

```
StoreController / AdminController (URL 매핑 + DTO 조립)
    ↓
Application Layer (비즈니스 로직 + 이벤트 발행)
    ↓
Core Modules (이벤트 리스너로 독립 동작)
    - UserCore: 유저 자동 생성
    - NotificationCore: 알림 발송
```

## 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Spring Boot 4.0.6 + Kotlin 2.2 + Spring Framework 7 |
| JVM | Java 21 (LTS) |
| DB | H2 파일 모드 (개발) → PostgreSQL (운영) |
| Frontend | Thymeleaf (SSR) |
| 빌드 | Gradle 8.14 (Kotlin DSL) |
| 인증 | 세션 기반 + SMS 인증코드 |

---

## 완료된 기능

### 고객 사이트 (StoreController)

| 기능 | URL | 상태 |
|---|---|---|
| 메인 페이지 (추천 코스 목록) | `/` | ✅ |
| 투어 상세 (이미지, 설명, 커스텀 연결) | `/tours/{id}` | ✅ |
| 커스텀 견적 요청 (캘린더 날짜 선택) | `/custom` | ✅ |
| 견적 요청 완료 | `/custom/complete` | ✅ |
| 로그인 (핸드폰 SMS 인증) | `/login` | ✅ |
| 마스터 계정 바로 로그인 | 01039941376 | ✅ |
| 예약 (캘린더 + 예약불가 날짜) | `/booking` | ✅ |
| 내 여행 목록 | `/my/trips` | ✅ |
| 여행 상세 (일정표 + 프린트) | `/my/trips/{id}` | ✅ |
| 캘린더 파일 다운로드 (.ics) | `/my/trips/{id}/calendar` | ✅ |

### 어드민 (AdminController)

| 기능 | URL | 상태 |
|---|---|---|
| 대시보드 (견적/여행/회원/상품 수) | `/admin` | ✅ |
| 견적 관리 (상태 필터링) | `/admin/quotes` | ✅ |
| 견적 상태 변경 | `/admin/quotes/{id}/status` | ✅ |
| 여행 등록 (견적 연결) | `/admin/trips` | ✅ |
| 여행 상세 | `/admin/trips/{id}` | ✅ |
| 투어 상품 목록 | `/admin/tours` | ✅ |
| 투어 상품 편집 (사진 업로드) | `/admin/tours/{id}` | ✅ |
| 투어 상품 추가 | `/admin/tours/new` | ✅ |
| 투어 활성/비활성 토글 | `/admin/tours/{id}/toggle` | ✅ |

### API

| 기능 | 엔드포인트 | 상태 |
|---|---|---|
| 인증코드 발송 | `POST /api/auth/send-code` | ✅ |
| 인증코드 검증 + 로그인 | `POST /api/auth/verify` | ✅ |
| 로그아웃 | `POST /api/auth/logout` | ✅ |
| 현재 유저 조회 | `GET /api/auth/me` | ✅ |
| 예약 불가 날짜 조회 | `GET /api/bookings/unavailable` | ✅ |
| 예약 생성 | `POST /api/bookings` | ✅ |

### 인프라/설정

| 항목 | 상태 |
|---|---|
| 멀티모듈 구조 | ✅ |
| 이벤트 기반 모듈 간 통신 | ✅ |
| ServiceResponse 통일 응답 | ✅ |
| GlobalExceptionHandler | ✅ |
| 어드민 접근 제어 (ADMIN role) | ✅ (임시 해제 중) |
| DevTools (핫 리로드) | ✅ |
| H2 파일 모드 (데이터 유지) | ✅ |
| GitHub 연동 | ✅ (sonomooo/gotomongol) |

---

## 추가 필요 기능

### 🔴 필수 (런칭 전)

| 기능 | 설명 | 난이도 |
|---|---|---|
| SMS 실제 발송 연동 | CoolSMS 또는 NHN Cloud 알림톡 | 중 |
| 어드민 로그인 UI | `/admin` 접속 시 로그인 화면 | 하 |
| 견적 → 여행 전환 버튼 | 견적 목록에서 "여행 등록" 클릭 시 정보 자동 채움 | 하 |
| 프론트엔드 디자인 | 현재 기본 CSS → 실제 서비스 수준 디자인 | 상 |
| 반응형 (모바일) | 모바일에서 깨지지 않게 | 중 |
| 카카오톡 채널 연동 | 문의 버튼 → 카카오톡 채널 | 하 |
| PostgreSQL 전환 | H2 → PostgreSQL (운영용) | 하 |
| 배포 (Railway/Render) | 도메인 연결 + HTTPS | 중 |

### 🟡 중요 (런칭 후 빠르게)

| 기능 | 설명 | 난이도 |
|---|---|---|
| 후기 게시판 | 사진 후기 작성/조회 | 중 |
| 여행 가이드 페이지 | 준비물, FAQ, 몽골 정보 | 하 |
| 이메일 발송 | 견적서/일정표 이메일 전송 | 중 |
| 어드민 유저 관리 | 유저 목록, 권한 변경 | 하 |
| 견적 상세 보기 | 어드민에서 견적 클릭 시 상세 정보 | 하 |
| SEO 기본 설정 | meta 태그, sitemap, robots.txt | 하 |

### 🟢 나중에 (사업 성장 후)

| 기능 | 설명 | 난이도 |
|---|---|---|
| 자동 견적 계산기 | 선택 항목 기반 실시간 가격 표시 | 상 |
| 결제 연동 (토스페이먼츠) | 예약금 온라인 결제 | 중 |
| 다국어 (영어) | 외국인 고객 대응 | 중 |
| 리뷰/평점 시스템 | 별점 + 텍스트 리뷰 | 중 |
| 알림톡 자동화 | 예약 확인, 출발 D-7 알림 등 | 중 |
| 프론트엔드 분리 (Next.js) | SSR → CSR 전환 | 상 |
| 모니터링/로깅 | Sentry, CloudWatch 등 | 중 |

---

## 실행 방법

```bash
# 서버 시작
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
cd ~/mongol-tour && ./gradlew :app-api:bootRun

# 서버 재시작
lsof -ti:8080 | xargs kill -9; export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home && cd ~/mongol-tour && ./gradlew :app-api:bootRun

# 빌드만
./gradlew :app-api:build -x test

# 테스트
./gradlew :app-api:test
```

## 접속 URL

| URL | 용도 |
|---|---|
| http://localhost:8080 | 고객 메인 |
| http://localhost:8080/admin | 어드민 |
| http://localhost:8080/h2-console | DB 콘솔 (JDBC URL: jdbc:h2:file:./data/gotomongol) |
