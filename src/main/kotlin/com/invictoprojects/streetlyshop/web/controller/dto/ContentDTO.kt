package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import java.time.Instant

data class ContentDTO(
    var id: String,
    var productId: String,
    var name: String,
    var description: String,
    var variantIds: List<String>,
    var variants: List<VariantDTO>,
    var attributes: List<AttributeDTO>,
    var creationDate: Instant,
    val modifiedDate: Instant
)

fun Content.toDTO(): ContentDTO {
    return ContentDTO(
        id = id.toString(),
        productId = productId.toString(),
        name = name,
        description = description,
        variantIds = variantIds.map { it.toString() },
        variants = variants.map { it.toDTO() },
        attributes = attributes.map { it.toDTO() },
        creationDate = creationDate,
        modifiedDate = modifiedDate
    )
}
