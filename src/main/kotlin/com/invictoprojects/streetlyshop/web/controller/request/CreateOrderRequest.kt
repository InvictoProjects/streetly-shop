package com.invictoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreateOrderRequest(
    @field:NotBlank
    val deliveryService: String? = null,
    @field:NotBlank
    val city: String? = null,
    @field:NotBlank
    val department: String? = null,
    @field:NotBlank
    val recipientName: String? = null,
    @field:NotBlank
    val recipientSurname: String? = null,
    @field:NotBlank
    val recipientMiddleName: String? = null,
    @field:NotBlank
    @field:Pattern(regexp = "^\\d{10,12}\$")
    val recipientPhone: String? = null,
    @field:Size(min = 1)
    val lines: List<OrderLineRequest> = emptyList()
)

data class OrderLineRequest(
    val variantId: String? = null,
    val quantity: Long = 1
)
