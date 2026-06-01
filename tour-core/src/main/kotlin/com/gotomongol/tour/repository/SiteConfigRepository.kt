package com.gotomongol.tour.repository

import com.gotomongol.tour.domain.SiteConfig
import org.springframework.data.jpa.repository.JpaRepository

interface SiteConfigRepository : JpaRepository<SiteConfig, Long> {
    fun findByConfigKey(key: String): SiteConfig?
}
