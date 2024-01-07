package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo
import java.time.Instant

data class VariantInfoDTO(
    val contentId: String,
    var productId: String,
    val product: ProductDTO? = null,
    var name: String = "",
    var description: String = "",
    var attributes: MutableList<AttributeDTO> = mutableListOf(),
    var variantIds: MutableList<String> = mutableListOf(),
    var variants: VariantDTO? = null,
    var languageCode: Language,
    val creationDate: Instant = Instant.now(),
    var modifiedDate: Instant = Instant.now(),
    val createdBy: String
)

fun VariantInfo.toDTO(): VariantInfoDTO {
    return VariantInfoDTO(
        contentId = contentId.toString(),
        productId = productId.toString(),
        product = product.toDTO(),
        name = name,
        description = description,
        attributes = attributes.map { it.toDTO() }.toMutableList(),
        variantIds = variantIds.map { it.toString() }.toMutableList(),
        variants = variants.toDTO(),
        languageCode = languageCode,
        creationDate = creationDate,
        modifiedDate = modifiedDate,
        createdBy = createdBy.toString()
    )
}
