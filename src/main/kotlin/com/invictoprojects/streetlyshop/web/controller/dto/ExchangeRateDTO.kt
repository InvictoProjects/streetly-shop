package com.invictoprojects.streetlyshop.web.controller.dto

import java.math.BigDecimal
import java.time.Instant

data class ExchangeRateDTO(
    var modifiedDate: Instant,
    var rate: BigDecimal
)
