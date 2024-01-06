package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ExchangeRateRepository
import com.invictoprojects.streetlyshop.persistence.VariantRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.ExchangeRate
import com.invictoprojects.streetlyshop.web.controller.dto.ExchangeRateDTO
import com.invictoprojects.streetlyshop.web.controller.request.UpdateExchangeRateRequest
import com.invictoprojects.streetlyshop.web.exception.ExchangeRateNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ExchangeRateService(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val variantRepository: VariantRepository
) {

    fun updateExchangeRate(request: UpdateExchangeRateRequest): ExchangeRateDTO {
        val exchangeRate = getByCurrency(request.currency!!)
        exchangeRate.updateRate(request.rate!!)

        variantRepository.updatePricesWithNewExchangeRate(request.currency, request.rate)
        return exchangeRateRepository.save(exchangeRate).toDTO()
    }

    fun getByCurrency(currency: Currency): ExchangeRate {
        val exchangeRateId = ExchangeRate.generateId(currency)

        return exchangeRateRepository.findByIdOrNull(exchangeRateId)
            ?: throw ExchangeRateNotFoundException("Exchange rate with id $exchangeRateId was not found")
    }

    fun getExchangeRateDTO(currency: Currency): ExchangeRateDTO {
        return getByCurrency(currency).toDTO()
    }
}

fun ExchangeRate.toDTO(): ExchangeRateDTO {
    return ExchangeRateDTO(
        modifiedDate = modifiedDate,
        rate = rate.bigDecimalValue()
    )
}
