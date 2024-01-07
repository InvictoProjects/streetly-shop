package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.order.OrderLine
import com.invictoprojects.streetlyshop.service.toDTO

data class OrderLineDTO(
    val id: String,
    val variantInfo: VariantInfoDTO,
    val quantity: Long
)

fun OrderLine.toDTO(): OrderLineDTO {
    return OrderLineDTO(id = id.toString(), variantInfo = variantInfo.toDTO(), quantity = quantity)
}
