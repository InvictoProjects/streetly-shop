package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.web.controller.dto.VariantDTO

class VariantService {
}

fun Variant.toDTO(): VariantDTO {
    return VariantDTO(
            id = id.toString(),
            barcode = barcode,
            productId = productId.toString(),
            contentId = contentId.toString(),
            medias = medias,
            attributes = attributes.map { it.toDTO() },
            creationDate = creationDate,
            modifiedDate = modifiedDate,
//            prices = prices.mapValues { (_, price) -> price.toDTO() },
//            stock = stock.toDTO()
    )
}
