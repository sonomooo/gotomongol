package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.SiteConfigEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaSiteConfigRepository : JpaRepository<SiteConfigEntity, Long> {
    fun findByConfigKey(configKey: String): SiteConfigEntity?
}
