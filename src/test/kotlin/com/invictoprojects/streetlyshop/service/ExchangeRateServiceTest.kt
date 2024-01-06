package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ExchangeRateRepository
import com.invictoprojects.streetlyshop.persistence.VariantRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.*
import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.ExchangeRate
import com.invictoprojects.streetlyshop.web.controller.request.UpdateExchangeRateRequest
import com.invictoprojects.streetlyshop.web.exception.ExchangeRateNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.bson.types.Decimal128
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.*


@ExtendWith(MockitoExtension::class)
internal class ExchangeRateServiceTest {

    @Mock
    lateinit var variantRepository: VariantRepository

    @Mock
    lateinit var exchangeRateRepository: ExchangeRateRepository

    @InjectMocks
    lateinit var exchangeRateService: ExchangeRateService

    @Test
    fun getByCurrency_currencyIsNotValid_exceptionIsThrown() {
        given(exchangeRateRepository.findById("UAH-EUR")).willReturn(Optional.empty())

        val throwable = catchThrowable { exchangeRateService.getByCurrency(Currency.EUR) }

        assertThat(throwable).isInstanceOf(ExchangeRateNotFoundException::class.java)
    }

    @Test
    fun getByCurrency_currencyIsValid_rateIsReturned() {
        val exchangeRate = ExchangeRate(
                id = "UAH-PLN",
                rate = Decimal128(1)
        )
        given(exchangeRateRepository.findById("UAH-PLN")).willReturn(Optional.of(exchangeRate))

        val actualExchangeRate = exchangeRateService.getByCurrency(Currency.PLN)

        assertThat(actualExchangeRate).isEqualTo(exchangeRate)
    }

    @Test
    fun getExchangeRateDTO_currencyIsValid_rateDTOIsReturned() {
        val exchangeRate = ExchangeRate(
                id = "UAH-PLN",
                rate = Decimal128(1)
        )
        given(exchangeRateRepository.findById("UAH-PLN")).willReturn(Optional.of(exchangeRate))

        val actualExchangeRateDTO = exchangeRateService.getExchangeRateDTO(Currency.PLN)

        assertThat(actualExchangeRateDTO.rate).isEqualTo(exchangeRate.rate.bigDecimalValue())
    }

    @Test
    fun updateExchangeRate_requestIsValid_pricesAreUpdated() {
        // given
        val request = UpdateExchangeRateRequest(
                rate = BigDecimal.TEN,
                currency = Currency.PLN
        )

        val exchangeRate = spy(
                ExchangeRate(
                        id = "UAH-PLN",
                        rate = Decimal128(1)
                )
        )
        given(exchangeRateRepository.findById("UAH-PLN")).willReturn(Optional.of(exchangeRate))
        given(exchangeRateRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<ExchangeRate>())

        // when
        val exchangeRateDTO = exchangeRateService.updateExchangeRate(request)

        // then
        verify(exchangeRate).updateRate(BigDecimal.TEN)
        verify(variantRepository).updatePricesWithNewExchangeRate(Currency.PLN, BigDecimal.TEN)

        assertThat(exchangeRateDTO.rate).isEqualTo(BigDecimal.TEN)
    }

    private fun <T> any(): T = Mockito.any()
}
