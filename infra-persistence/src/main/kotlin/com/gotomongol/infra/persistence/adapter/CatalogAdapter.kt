package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.ActivityItemPort
import com.gotomongol.domain.port.FoodItemPort
import com.gotomongol.domain.port.SpotItemPort
import com.gotomongol.domain.tour.ActivityItem
import com.gotomongol.domain.tour.FoodItem
import com.gotomongol.domain.tour.SpotItem
import com.gotomongol.infra.persistence.entity.ActivityItemEntity
import com.gotomongol.infra.persistence.entity.FoodItemEntity
import com.gotomongol.infra.persistence.entity.SpotItemEntity
import com.gotomongol.infra.persistence.repository.JpaActivityItemRepository
import com.gotomongol.infra.persistence.repository.JpaFoodItemRepository
import com.gotomongol.infra.persistence.repository.JpaSpotItemRepository
import org.springframework.stereotype.Component

@Component
class SpotItemAdapter(private val repo: JpaSpotItemRepository) : SpotItemPort {
    override fun findAll(): List<SpotItem> { return repo.findAll().map { SpotItem(it.id, it.name, it.price, it.active) } }
    override fun findActive(): List<SpotItem> { return repo.findByActiveTrue().map { SpotItem(it.id, it.name, it.price, it.active) } }
    override fun save(item: SpotItem): SpotItem { val e = repo.save(SpotItemEntity(item.id, item.name, item.price, item.active)); return SpotItem(e.id, e.name, e.price, e.active) }
    override fun deleteById(id: Long) { repo.deleteById(id) }
}

@Component
class ActivityItemAdapter(private val repo: JpaActivityItemRepository) : ActivityItemPort {
    override fun findAll(): List<ActivityItem> { return repo.findAll().map { ActivityItem(it.id, it.name, it.price, it.active) } }
    override fun findActive(): List<ActivityItem> { return repo.findByActiveTrue().map { ActivityItem(it.id, it.name, it.price, it.active) } }
    override fun save(item: ActivityItem): ActivityItem { val e = repo.save(ActivityItemEntity(item.id, item.name, item.price, item.active)); return ActivityItem(e.id, e.name, e.price, e.active) }
    override fun deleteById(id: Long) { repo.deleteById(id) }
}

@Component
class FoodItemAdapter(private val repo: JpaFoodItemRepository) : FoodItemPort {
    override fun findAll(): List<FoodItem> { return repo.findAll().map { FoodItem(it.id, it.name, it.price, it.active) } }
    override fun findActive(): List<FoodItem> { return repo.findByActiveTrue().map { FoodItem(it.id, it.name, it.price, it.active) } }
    override fun save(item: FoodItem): FoodItem { val e = repo.save(FoodItemEntity(item.id, item.name, item.price, item.active)); return FoodItem(e.id, e.name, e.price, e.active) }
    override fun deleteById(id: Long) { repo.deleteById(id) }
}
