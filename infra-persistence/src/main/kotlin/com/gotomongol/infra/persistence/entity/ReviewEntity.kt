package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "reviews")
class ReviewEntity(
    val userId: Long,
    val tourName: String,
    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String,

    var rating: Int = 5,

    @ElementCollection
    @CollectionTable(name = "review_image_urls", joinColumns = [JoinColumn(name = "review_id")])
    @Column(name = "image_url")
    var imageUrls: List<String> = emptyList(),

    var visible: Boolean = true
) : BaseEntity()
