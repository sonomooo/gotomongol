package com.gotomongol.domain.review

data class Review(
    val id: Long = 0,
    val userId: Long,
    val tourName: String,
    val title: String,
    val content: String,
    val rating: Int = 5,
    val imageUrls: List<String> = emptyList(),
    val visible: Boolean = true
)
