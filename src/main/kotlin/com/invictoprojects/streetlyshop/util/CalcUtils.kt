package com.invictoprojects.streetlyshop.util;

import org.bson.types.Decimal128
import java.math.BigDecimal

fun BigDecimal.toDecimal128(): Decimal128 {
    return Decimal128(this)
}
