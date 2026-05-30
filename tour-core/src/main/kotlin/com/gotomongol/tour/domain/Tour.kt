package com.gotomongol.tour.domain

import com.gotomongol.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "tours")
class Tour(
    var name: String,
    var days: Int,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(name = "min_price")
    var minPrice: Int,

    @Column(name = "max_price")
    var maxPrice: Int,

    @ElementCollection
    @CollectionTable(name = "tour_spots", joinColumns = [JoinColumn(name = "tour_id")])
    @Column(name = "spot")
    var spots: MutableList<String> = mutableListOf(),

    @ElementCollection
    @CollectionTable(name = "tour_activities", joinColumns = [JoinColumn(name = "tour_id")])
    @Column(name = "activity")
    var activities: MutableList<String> = mutableListOf(),

    var imageUrl: String? = null,

    @Column(columnDefinition = "TEXT")
    var detailContent: String? = null,

    var active: Boolean = true
) : BaseEntity()
