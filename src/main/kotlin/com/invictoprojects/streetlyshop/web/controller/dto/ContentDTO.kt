package com.invictoprojects.streetlyshop.web.controller.dto

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
