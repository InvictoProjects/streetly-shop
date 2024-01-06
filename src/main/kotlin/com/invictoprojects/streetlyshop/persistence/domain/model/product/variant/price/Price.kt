package com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price

import org.bson.types.Decimal128

data class Price(
    var salePrice: Decimal128,
    var originalPrice: Decimal128
)
