package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
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
