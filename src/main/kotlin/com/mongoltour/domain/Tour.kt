package com.mongoltour.domain

import jakarta.persistence.*

@Entity
@Table(name = "tours")
class Tour(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    val days: Int,

    val description: String,

    @Column(name = "min_price")
    val minPrice: Int,

    @Column(name = "max_price")
    val maxPrice: Int,

    @ElementCollection
    @CollectionTable(name = "tour_spots", joinColumns = [JoinColumn(name = "tour_id")])
    @Column(name = "spot")
    val spots: List<String> = emptyList(),

    @ElementCollection
    @CollectionTable(name = "tour_activities", joinColumns = [JoinColumn(name = "tour_id")])
    @Column(name = "activity")
    val activities: List<String> = emptyList(),

    val active: Boolean = true
)
