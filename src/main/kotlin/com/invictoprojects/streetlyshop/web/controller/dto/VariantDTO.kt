package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
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
    val prices: Map<Currency, PriceDTO>,
    val stock: StockDTO
)

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
        prices = prices.mapValues { (_, price) -> price.toDTO() },
        stock = stock.toDTO()
    )
}
