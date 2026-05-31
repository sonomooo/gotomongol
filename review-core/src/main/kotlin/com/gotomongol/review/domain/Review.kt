package com.gotomongol.review.domain

import com.gotomongol.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "reviews")
class Review(
    val userId: Long,
    val tourName: String,
    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String,

    var rating: Int = 5,

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = [JoinColumn(name = "review_id")])
    @Column(name = "image_url")
    var imageUrls: MutableList<String> = mutableListOf(),

    var visible: Boolean = true
) : BaseEntity()
