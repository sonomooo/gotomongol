package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "board_posts")
class BoardPostEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val category: String = "",
    var title: String = "",
    @Column(columnDefinition = "TEXT")
    var content: String = "",
    var imageUrl: String? = null,
    var active: Boolean = true
)
