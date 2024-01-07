package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.Price
import java.math.BigDecimal

data class PriceDTO(
    var salePrice: BigDecimal,
    var originalPrice: BigDecimal
)

fun Price.toDTO(): PriceDTO {
    return PriceDTO(salePrice = salePrice.bigDecimalValue(), originalPrice = originalPrice.bigDecimalValue())
}
