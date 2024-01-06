package com.invictoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.NotNull

data class UpdateStockRequest(
    @field:NotNull
    val stockDelta: Long?
)
