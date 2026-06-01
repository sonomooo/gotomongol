package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "price_configs")
class PriceConfigEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    val category: String,

    val itemName: String,

    val pricePerUnit: Int,

    val unit: String = "1일"
)
