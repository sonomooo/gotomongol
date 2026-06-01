package com.gotomongol.domain.tour

data class Tour(
    val id: Long = 0,
    val name: String,
    val days: Int,
    val description: String,
    val minPrice: Int,
    val maxPrice: Int,
    val spots: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val imageUrl: String? = null,
    val imageUrls: List<String> = emptyList(),
    val detailContent: String? = null,
    val active: Boolean = true
)
