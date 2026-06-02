package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.board.BoardPost
import com.gotomongol.domain.port.BoardPostPort
import com.gotomongol.infra.persistence.entity.BoardPostEntity
import com.gotomongol.infra.persistence.repository.JpaBoardPostRepository
import org.springframework.stereotype.Component

@Component
class BoardPostAdapter(private val repo: JpaBoardPostRepository) : BoardPostPort {
    override fun findByCategory(category: String): List<BoardPost> {
        return repo.findByCategoryAndActiveTrueOrderByIdDesc(category).map { toDomain(it) }
    }
    override fun findById(id: Long): BoardPost? {
        return repo.findById(id).orElse(null)?.let { toDomain(it) }
    }
    override fun findAll(): List<BoardPost> {
        return repo.findAll().map { toDomain(it) }
    }
    override fun save(post: BoardPost): BoardPost {
        return toDomain(repo.save(BoardPostEntity(post.id, post.category.name, post.title, post.content, post.imageUrl, post.active)))
    }
    override fun deleteById(id: Long) { repo.deleteById(id) }

    private fun toDomain(e: BoardPostEntity): BoardPost {
        return BoardPost(e.id, com.gotomongol.domain.board.BoardCategory.valueOf(e.category), e.title, e.content, e.imageUrl, e.active)
    }
}
