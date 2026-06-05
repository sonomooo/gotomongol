package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.ActivityItemEntity
import com.gotomongol.infra.persistence.entity.FoodItemEntity
import com.gotomongol.infra.persistence.entity.SpotItemEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaSpotItemRepository : JpaRepository<SpotItemEntity, Long> {
    fun findByActiveTrue(): List<SpotItemEntity>
}

interface JpaActivityItemRepository : JpaRepository<ActivityItemEntity, Long> {
    fun findByActiveTrue(): List<ActivityItemEntity>
}

interface JpaFoodItemRepository : JpaRepository<FoodItemEntity, Long> {
    fun findByActiveTrue(): List<FoodItemEntity>
}
