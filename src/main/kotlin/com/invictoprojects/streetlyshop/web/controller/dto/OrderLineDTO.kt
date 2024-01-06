package com.invictoprojects.streetlyshop.web.controller.dto

data class OrderLineDTO(
        val id: String,
        val variantInfo: VariantInfoDTO,
        val quantity: Long
)
