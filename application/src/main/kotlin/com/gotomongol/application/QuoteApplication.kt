package com.gotomongol.application

import com.gotomongol.application.dto.QuoteSubmitCommand
import com.gotomongol.domain.event.QuoteSubmittedEvent
import com.gotomongol.domain.port.ActivityItemPort
import com.gotomongol.domain.port.FoodItemPort
import com.gotomongol.domain.port.PriceConfigPort
import com.gotomongol.domain.port.QuoteRequestPort
import com.gotomongol.domain.port.SpotItemPort
import com.gotomongol.domain.tour.AccommodationType
import com.gotomongol.domain.tour.PriceCategory
import com.gotomongol.domain.tour.PriceConfig
import com.gotomongol.domain.tour.QuoteRequest
import com.gotomongol.domain.tour.QuoteStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuoteApplication(
    private val quoteRequestPort: QuoteRequestPort,
    private val priceConfigPort: PriceConfigPort,
    private val spotItemPort: SpotItemPort,
    private val activityItemPort: ActivityItemPort,
    private val foodItemPort: FoodItemPort,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun submit(cmd: QuoteSubmitCommand): QuoteRequest {
        val quote = quoteRequestPort.save(
            QuoteRequest(
                customerName = cmd.customerName, phone = cmd.phone, email = cmd.email,
                days = cmd.days, groupSize = cmd.groupSize, preferredDate = cmd.preferredDate,
                spots = cmd.spots, activities = cmd.activities,
                accommodationType = AccommodationType.valueOf(cmd.accommodationType), memo = cmd.memo
            )
        )
        eventPublisher.publishEvent(QuoteSubmittedEvent(quote.id, cmd.phone, cmd.customerName))
        return quote
    }

    fun estimate(days: Int, groupSize: Int, spots: List<String>, activities: List<String>, foods: List<String>, accommodation: String): Map<String, Any> {
        val configs = priceConfigPort.findAll()
        val configMap = configs.associateBy { "${it.category}_${it.itemName}" }

        var total = 0
        val breakdown = mutableListOf<Map<String, Any>>()

        // 기본 비용 (차량+가이드) × 일수 ÷ 인원
        val vehiclePrice = configMap["BASE_차량+기사"]?.pricePerUnit ?: 180000
        val guidePrice = configMap["BASE_가이드"]?.pricePerUnit ?: 100000
        val baseCost = (vehiclePrice + guidePrice) * days / groupSize
        total += baseCost
        breakdown.add(mapOf("item" to "기본 (차량+가이드)", "amount" to baseCost))

        // 숙소 × 박수
        val accPrice = configMap["ACCOMMODATION_$accommodation"]?.pricePerUnit
            ?: when (accommodation) { "PREMIUM_CAMP" -> 100000; "HOTEL_MIX" -> 120000; else -> 40000 }
        val accCost = accPrice * (days - 1)
        total += accCost
        breakdown.add(mapOf("item" to "숙소 (${days - 1}박)", "amount" to accCost))

        // 식비
        val mealPrice = configMap["BASE_식비"]?.pricePerUnit ?: 25000
        val mealCost = mealPrice * days
        total += mealCost
        breakdown.add(mapOf("item" to "식비 (${days}일)", "amount" to mealCost))

        // 스팟별 추가
        val spotPriceMap = spotItemPort.findActive().associateBy { it.name }
        spots.forEach { spot ->
            val price = spotPriceMap[spot]?.price ?: configMap["SPOT_$spot"]?.pricePerUnit ?: 0
            if (price > 0) {
                total += price
                breakdown.add(mapOf("item" to spot, "amount" to price))
            }
        }

        // 액티비티별 추가
        val actPriceMap = activityItemPort.findActive().associateBy { it.name }
        activities.forEach { activity ->
            val price = actPriceMap[activity]?.price ?: configMap["ACTIVITY_$activity"]?.pricePerUnit ?: 30000
            total += price
            breakdown.add(mapOf("item" to activity, "amount" to price))
        }

        // 음식별 추가
        val foodPriceMap = foodItemPort.findActive().associateBy { it.name }
        foods.forEach { food ->
            val price = foodPriceMap[food]?.price ?: 0
            if (price > 0) {
                total += price
                breakdown.add(mapOf("item" to food, "amount" to price))
            }
        }

        return mapOf("total" to total, "breakdown" to breakdown, "perPerson" to total)
    }

    fun getAllPriceConfigs(): List<PriceConfig> {
        return priceConfigPort.findAll()
    }

    fun savePriceConfig(config: PriceConfig): PriceConfig {
        return priceConfigPort.save(config)
    }

    fun deletePriceConfig(id: Long) {
        priceConfigPort.deleteById(id)
    }

    fun updateStatus(id: Long, status: QuoteStatus) {
        quoteRequestPort.updateStatus(id, status)
    }

    fun findAll(): List<QuoteRequest> {
        return quoteRequestPort.findAll()
    }

    fun findByStatus(status: QuoteStatus): List<QuoteRequest> {
        return quoteRequestPort.findByStatus(status)
    }
}
