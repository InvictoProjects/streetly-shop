package com.invictoprojects.streetlyshop.persistence.domain.model.product

import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("reviews")
data class Review(
    @field:Id
    val id: ObjectId,
    val productId: ObjectId,
    val contentId: ObjectId,
    val variantId: ObjectId,
    val createdBy: ObjectId,
    val customerName: String? = null,
    val customerAvatar: String? = null,
    val creationDate: Instant = Instant.now(),
    var medias: MutableList<Media> = mutableListOf(),
    val text: String,
    val rating: Int
)
