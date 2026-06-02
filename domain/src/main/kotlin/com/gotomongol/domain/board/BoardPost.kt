package com.gotomongol.domain.board

data class BoardPost(
    val id: Long = 0,
    val category: BoardCategory,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    val active: Boolean = true
)

enum class BoardCategory {
    NOTICE,      // 공지사항
    WELCOME,     // 몽골로 오세요
    SPOT_INFO,   // 관광지
    ACTIVITY_INFO // 활동
}
