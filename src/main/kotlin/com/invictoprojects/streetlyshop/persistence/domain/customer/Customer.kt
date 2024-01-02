package com.invictoprojects.streetlyshop.persistence.domain.customer

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant


@Document
data class Customer(
    @field:Id
    val id: ObjectId? = null,
    var name: String? = null,
    var surname: String? = null,
    var middleName: String? = null,
    var avatar: String? = null,
    var phone: String? = null,
    var birthDay: Long? = null,
    var gender: Gender? = null,
    var nickname: String,
    var email: String,
    var password: String,
    var roles: List<Role> = mutableListOf(),
    var registeredAt: Instant = Instant.now(),
    var favoriteProductIds: MutableSet<ObjectId> = mutableSetOf()
) {
    fun getAvatarFileName(): String? {
        return avatar?.substringAfterLast("/")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Customer) return false
        return id == other.id
                && email == other.email
                && avatar == other.avatar
                && name == other.name
                && surname == other.surname
                && nickname == other.nickname
                && password == other.password
                && roles == other.roles
                && phone == other.phone
                && birthDay == other.birthDay
                && middleName == other.middleName
                && gender == other.gender
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (surname?.hashCode() ?: 0)
        result = 31 * result + (middleName?.hashCode() ?: 0)
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        result = 31 * result + (birthDay?.hashCode() ?: 0)
        result = 31 * result + (gender?.hashCode() ?: 0)
        result = 31 * result + nickname.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + roles.hashCode()
        result = 31 * result + registeredAt.hashCode()
        result = 31 * result + favoriteProductIds.hashCode()
        return result
    }
}
