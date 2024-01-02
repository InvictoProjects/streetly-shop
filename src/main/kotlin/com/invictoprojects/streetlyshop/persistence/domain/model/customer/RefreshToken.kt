package com.invictoprojects.streetlyshop.persistence.domain.model.customer

import net.minidev.json.annotate.JsonIgnore
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("refreshTokens")
data class RefreshToken(
    @field:Id
    val id: ObjectId,
    val token: String,
    val expiration: Date
) {

    @JsonIgnore
    fun isExpired() = Date().after(expiration)
}
