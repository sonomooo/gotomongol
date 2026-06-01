package com.gotomongol.domain.tour

data class PriceConfig(
    val id: Long = 0,
    val category: PriceCategory,
    val itemName: String,
    val pricePerUnit: Int,
    val unit: String = "1일"
)

enum class PriceCategory {
    BASE,        // 기본 (차량+기사, 가이드)
    SPOT,        // 방문지별 추가
    ACTIVITY,    // 액티비티별
    ACCOMMODATION // 숙소 등급별
}
