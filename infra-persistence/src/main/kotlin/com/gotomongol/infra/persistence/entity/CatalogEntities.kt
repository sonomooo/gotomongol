package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "spot_items")
class SpotItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    val active: Boolean = true
)

@Entity
@Table(name = "activity_items")
class ActivityItemEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    val active: Boolean = true
)
