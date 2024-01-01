package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantMedia
import java.time.Instant

data class VariantDTO(
    var id: String,
    var barcode: String,
    var productId: String,
    var contentId: String,
    var medias: List<VariantMedia>,
    var attributes: List<AttributeDTO>,
    var creationDate: Instant,
    val modifiedDate: Instant,
)
