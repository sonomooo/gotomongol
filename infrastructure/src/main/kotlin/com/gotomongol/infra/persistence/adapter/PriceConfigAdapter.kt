package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.PriceConfigPort
import com.gotomongol.domain.tour.PriceCategory
import com.gotomongol.domain.tour.PriceConfig
import com.gotomongol.infra.persistence.entity.PriceConfigEntity
import com.gotomongol.infra.persistence.repository.JpaPriceConfigRepository
import org.springframework.stereotype.Component

@Component
class PriceConfigAdapter(private val repo: JpaPriceConfigRepository) : PriceConfigPort {

    override fun findAll(): List<PriceConfig> {
        return repo.findAll().map { toDomain(it) }
    }

    override fun findByCategory(category: PriceCategory): List<PriceConfig> {
        return repo.findByCategory(category.name).map { toDomain(it) }
    }

    override fun save(config: PriceConfig): PriceConfig {
        return toDomain(repo.save(toEntity(config)))
    }

    override fun deleteById(id: Long) {
        repo.deleteById(id)
    }

    private fun toDomain(e: PriceConfigEntity): PriceConfig {
        return PriceConfig(id = e.id, category = PriceCategory.valueOf(e.category),
            itemName = e.itemName, pricePerUnit = e.pricePerUnit, unit = e.unit)
    }

    private fun toEntity(d: PriceConfig): PriceConfigEntity {
        return PriceConfigEntity(id = d.id, category = d.category.name,
            itemName = d.itemName, pricePerUnit = d.pricePerUnit, unit = d.unit)
    }
}
