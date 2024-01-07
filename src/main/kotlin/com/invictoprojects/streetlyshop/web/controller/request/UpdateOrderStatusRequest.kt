package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.order.OrderStatus
import javax.validation.constraints.NotNull

data class UpdateOrderStatusRequest(
    @field:NotNull
    val status: OrderStatus? = null
)
