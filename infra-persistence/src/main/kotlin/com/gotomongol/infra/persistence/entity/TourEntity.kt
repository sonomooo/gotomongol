package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "tours")
class TourEntity(
    var name: String,
    var days: Int,

    @Column(columnDefinition = "TEXT")
    var description: String,

    var minPrice: Int,
    var maxPrice: Int,

    @ElementCollection
    @CollectionTable(name = "tour_spots", joinColumns = [JoinColumn(name = "tour_id")])
    @Column(name = "spot")
    var spots: List<String> = emptyList(),

    @ElementCollection
    @CollectionTable(name = "tour_activities", joinColumns = [JoinColumn(name = "tour_id")])
    @Column(name = "activity")
    var activities: List<String> = emptyList(),

    var imageUrl: String? = null,

    @ElementCollection
    @CollectionTable(name = "tour_image_urls", joinColumns = [JoinColumn(name = "tour_id")])
    @Column(name = "image_url")
    var imageUrls: List<String> = emptyList(),

    @Column(columnDefinition = "TEXT")
    var detailContent: String? = null,

    var active: Boolean = true
) : BaseEntity()
