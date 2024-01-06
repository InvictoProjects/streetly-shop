package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.service.ExchangeRateService
import com.invictoprojects.streetlyshop.web.controller.dto.ExchangeRateDTO
import com.invictoprojects.streetlyshop.web.controller.request.UpdateExchangeRateRequest
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class ExchangeRateControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var exchangeRateService: ExchangeRateService

    @InjectMocks
    lateinit var controller: ExchangeRateController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun updateRate_rateIsNull_badRequestIsReturned() {
        val request = UpdateExchangeRateRequest(rate = null, currency = Currency.EUR)

        mockMvc.perform(
                put("/v1/api/exchange-rate").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(exchangeRateService)
    }

    @Test
    fun updateRate_rateIsBelowZero_badRequestIsReturned() {
        val request = UpdateExchangeRateRequest(rate = BigDecimal(-1), currency = Currency.EUR)

        mockMvc.perform(
                put("/v1/api/exchange-rate").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(exchangeRateService)
    }

    @Test
    fun updateRate_currencyIsNull_badRequestIsReturned() {
        val request = UpdateExchangeRateRequest(rate = BigDecimal.ONE, currency = null)

        mockMvc.perform(
                put("/v1/api/exchange-rate").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(exchangeRateService)
    }

    @Test
    fun updateRate_requestIsValid_rateIsUpdated() {
        val request = UpdateExchangeRateRequest(rate = BigDecimal.ONE, currency = Currency.EUR)

        mockMvc.perform(
                put("/v1/api/exchange-rate").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(exchangeRateService).updateExchangeRate(request)
    }

    @Test
    fun getExchangeRate_pathIsValid_rateIsReturned() {
        val rateDTO = ExchangeRateDTO(modifiedDate = Instant.now(), rate = BigDecimal.TEN)

        given(exchangeRateService.getExchangeRateDTO(Currency.EUR)).willReturn(rateDTO)

        val actualResponse = mockMvc.perform(
                get("/v1/api/exchange-rate/EUR")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk)
                .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
                .isEqualTo(objectMapper.writeValueAsString(rateDTO))
    }
}
