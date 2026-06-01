package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.SiteConfigPort
import com.gotomongol.domain.tour.SiteConfig
import com.gotomongol.infra.persistence.entity.SiteConfigEntity
import com.gotomongol.infra.persistence.repository.JpaSiteConfigRepository
import org.springframework.stereotype.Component

@Component
class SiteConfigAdapter(private val repo: JpaSiteConfigRepository) : SiteConfigPort {

    override fun findByKey(key: String): SiteConfig? = repo.findByConfigKey(key)?.toDomain()

    override fun save(config: SiteConfig): SiteConfig = repo.save(config.toEntity()).toDomain()

    override fun findAll(): List<SiteConfig> = repo.findAll().map { it.toDomain() }

    private fun SiteConfigEntity.toDomain() = SiteConfig(
        id = id, configKey = configKey, configValue = configValue
    )

    private fun SiteConfig.toEntity() = SiteConfigEntity(
        configKey = configKey, configValue = configValue
    ).also { if (id != 0L) setId(it, id) }

    private fun setId(entity: SiteConfigEntity, id: Long) {
        val field = entity.javaClass.superclass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }
}
