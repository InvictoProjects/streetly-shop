package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Stock

data class StockDTO(
    var quantity: Long
)

fun Stock.toDTO(): StockDTO {
    return StockDTO(quantity)
}
