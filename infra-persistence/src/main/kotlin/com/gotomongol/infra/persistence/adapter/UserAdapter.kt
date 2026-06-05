package com.gotomongol.infra.persistence.adapter

import com.gotomongol.domain.port.UserPort
import com.gotomongol.domain.user.User
import com.gotomongol.infra.persistence.entity.UserEntity
import com.gotomongol.infra.persistence.entity.UserRole
import com.gotomongol.infra.persistence.repository.JpaUserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserAdapter(private val repo: JpaUserRepository) : UserPort {

    override fun findByPhone(phone: String): User? = repo.findByPhone(phone)?.toDomain()

    override fun findById(id: Long): User? = repo.findByIdOrNull(id)?.toDomain()

    override fun save(user: User): User = repo.save(user.toEntity()).toDomain()

    override fun existsByPhone(phone: String): Boolean = repo.existsByPhone(phone)

    override fun count(): Long = repo.count()

    private fun UserEntity.toDomain() = User(
        id = id, name = name, phone = phone, email = email,
        role = com.gotomongol.domain.user.UserRole.valueOf(role.name),
        termsAgreed = termsAgreed, privacyAgreed = privacyAgreed, marketingAgreed = marketingAgreed
    )

    private fun User.toEntity() = UserEntity(
        name = name, phone = phone, email = email,
        role = UserRole.valueOf(role.name),
        termsAgreed = termsAgreed, privacyAgreed = privacyAgreed, marketingAgreed = marketingAgreed
    ).also { if (id != 0L) setId(it, id) }

    private fun setId(entity: UserEntity, id: Long) {
        val field = entity.javaClass.superclass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }
}
