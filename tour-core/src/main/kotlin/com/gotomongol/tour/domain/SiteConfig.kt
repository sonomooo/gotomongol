package com.gotomongol.tour.domain

import com.gotomongol.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "site_config")
class SiteConfig(
    @Column(unique = true)
    val configKey: String,

    @Column(columnDefinition = "TEXT")
    var configValue: String
) : BaseEntity()
