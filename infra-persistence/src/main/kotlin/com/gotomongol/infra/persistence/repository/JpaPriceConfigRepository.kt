package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.PriceConfigEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaPriceConfigRepository : JpaRepository<PriceConfigEntity, Long> {
    fun findByCategory(category: String): List<PriceConfigEntity>
}
