package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.Review
import java.time.Instant

data class ReviewDTO(
    val id: String,
    val productId: String,
    val contentId: String,
    val variantId: String,
    val createdBy: String,
    val customerName: String?,
    val customerAvatar: String?,
    val creationDate: Instant,
    var medias: MutableList<MediaDTO>,
    val text: String,
    val rating: Int
)

fun Review.toDTO(): ReviewDTO {
    return ReviewDTO(
        id = id.toString(),
        productId = productId.toString(),
        contentId = contentId.toString(),
        variantId = variantId.toString(),
        createdBy = createdBy.toString(),
        customerName = customerName,
        customerAvatar = customerAvatar,
        creationDate = creationDate,
        medias = medias.map { it.toDTO() }.toMutableList(),
        text = text,
        rating = rating
    )
}
