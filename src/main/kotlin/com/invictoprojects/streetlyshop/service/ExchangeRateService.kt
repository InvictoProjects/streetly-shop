package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.ExchangeRate
import org.springframework.stereotype.Service

@Service
class ExchangeRateService(
) {

    fun getByCurrency(currency: Currency): ExchangeRate {
        TODO()
    }

}
