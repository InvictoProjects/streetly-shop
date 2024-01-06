package com.invictoprojects.streetlyshop.web.controller.dto

import java.math.BigDecimal

data class PriceDTO(
    var salePrice: BigDecimal,
    var originalPrice: BigDecimal
)
