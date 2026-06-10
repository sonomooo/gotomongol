package com.gotomongol.infra.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "site_config")
class SiteConfigEntity(
    @Column(unique = true)
    val configKey: String,

    @Column(columnDefinition = "TEXT")
    var configValue: String
) : BaseEntity()
