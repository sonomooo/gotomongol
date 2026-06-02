package com.gotomongol.infra.persistence.repository

import com.gotomongol.infra.persistence.entity.BoardPostEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaBoardPostRepository : JpaRepository<BoardPostEntity, Long> {
    fun findByCategoryAndActiveTrueOrderByIdDesc(category: String): List<BoardPostEntity>
}
