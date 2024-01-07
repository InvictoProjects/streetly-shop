package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.ExchangeRate
import java.math.BigDecimal
import java.time.Instant

data class ExchangeRateDTO(
    var modifiedDate: Instant,
    var rate: BigDecimal
)

fun ExchangeRate.toDTO(): ExchangeRateDTO {
    return ExchangeRateDTO(
        modifiedDate = modifiedDate,
        rate = rate.bigDecimalValue()
    )
}
