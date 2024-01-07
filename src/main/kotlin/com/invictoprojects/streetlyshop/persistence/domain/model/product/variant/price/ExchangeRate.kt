package com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price

import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.service.toDecimal128
import org.bson.types.Decimal128
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document("exchangeRates")
data class ExchangeRate(
    @field:Id
    val id: String,
    var modifiedDate: Instant = Instant.now(),
    var rate: Decimal128
) {
    fun updateRate(rate: BigDecimal): ExchangeRate {
        this.rate = rate.toDecimal128()
        modifiedDate = Instant.now()
        return this
    }

    companion object {
        fun generateId(currency: Currency) = "${Currency.UAH}-${currency}"
    }
}
