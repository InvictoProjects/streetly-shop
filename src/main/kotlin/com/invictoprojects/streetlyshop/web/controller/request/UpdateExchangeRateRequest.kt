package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.NotNull

data class UpdateExchangeRateRequest(
        @field:NotNull
        @field:DecimalMin("0")
        val rate: BigDecimal?,
        @field:NotNull
        val currency: Currency?
)
