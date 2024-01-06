package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.service.ExchangeRateService
import com.invictoprojects.streetlyshop.web.controller.dto.ExchangeRateDTO
import com.invictoprojects.streetlyshop.web.controller.request.UpdateExchangeRateRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Api("Exchange Rate Controller")
@Validated
@RestController
@RequestMapping("/v1/api/exchange-rate")
class ExchangeRateController(
        val exchangeRateService: ExchangeRateService
) {

    @ApiOperation("Update exchange rate")
    @PutMapping
    fun updateRate(@Valid @RequestBody request: UpdateExchangeRateRequest) =
            exchangeRateService.updateExchangeRate(request)

    @ApiOperation("Get exchange rate by currency")
    @GetMapping("{currency}")
    fun getExchangeRate(@PathVariable currency: Currency): ExchangeRateDTO =
            exchangeRateService.getExchangeRateDTO(currency)
}
