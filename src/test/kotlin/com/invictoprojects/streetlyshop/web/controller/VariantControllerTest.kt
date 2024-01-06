package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantMedia
import com.invictoprojects.streetlyshop.service.VariantService
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.controller.dto.PriceDTO
import com.invictoprojects.streetlyshop.web.controller.dto.StockDTO
import com.invictoprojects.streetlyshop.web.controller.dto.VariantDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateVariantRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateStockRequest
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
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
internal class VariantControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var variantService: VariantService

    @InjectMocks
    lateinit var controller: VariantController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun createVariant_contentIdIsBlank_badRequestIsReturned() {
        val request = CreateVariantRequest(
            contentId = " ",
            barcode = "123124",
            attributes = mutableListOf(AttributeDTO(ObjectId().toString(), ObjectId().toString()))
        )

        mockMvc.perform(
            post("/v1/api/variant").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(variantService)
    }

    @Test
    fun createVariant_barcodeIsBlank_badRequestIsReturned() {
        val request = CreateVariantRequest(
            contentId = "6460ca81e6192a239603be01",
            barcode = " ",
            attributes = mutableListOf(AttributeDTO(ObjectId().toString(), ObjectId().toString()))
        )

        mockMvc.perform(
            post("/v1/api/variant").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(variantService)
    }

    @Test
    fun createVariant_salePriceIsNull_badRequestIsReturned() {
        val request = CreateVariantRequest(
            contentId = "6460ca81e6192a239603be01",
            barcode = "123124",
            attributes = mutableListOf(AttributeDTO(ObjectId().toString(), ObjectId().toString())),
            originalPriceUAH = BigDecimal.ONE
        )

        mockMvc.perform(
            post("/v1/api/variant").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(variantService)
    }

    @Test
    fun createVariant_originalPriceIsNull_badRequestIsReturned() {
        val request = CreateVariantRequest(
            contentId = "6460ca81e6192a239603be01",
            barcode = "123124",
            attributes = mutableListOf(AttributeDTO(ObjectId().toString(), ObjectId().toString())),
            salePriceUAH = BigDecimal.ONE
        )

        mockMvc.perform(
            post("/v1/api/variant").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(variantService)
    }

    @Test
    fun createVariant_stockIsLessThanZero_badRequestIsReturned() {
        val request = CreateVariantRequest(
            contentId = "6460ca81e6192a239603be01",
            barcode = "123124",
            attributes = mutableListOf(AttributeDTO(ObjectId().toString(), ObjectId().toString())),
            salePriceUAH = BigDecimal.ONE,
            originalPriceUAH = BigDecimal.TEN,
            stockQuantity = -1
        )

        mockMvc.perform(
            post("/v1/api/variant").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(variantService)
    }

    @Test
    fun createVariant_requestIsValid_variantIsCreated() {
        val request = CreateVariantRequest(
            contentId = "6460ca81e6192a239603be01",
            barcode = "1231245",
            attributes = mutableListOf(AttributeDTO(ObjectId().toString(), ObjectId().toString())),
            salePriceUAH = BigDecimal.ONE,
            originalPriceUAH = BigDecimal.ONE,
            stockQuantity = 10
        )

        mockMvc.perform(
            post("/v1/api/variant").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(variantService).createVariant(request)
    }

    @Test
    fun getVariant_languageIsInvalid_badRequestIsReturned() {
        val variantId = ObjectId().toString()
        val language = "invalid"

        mockMvc.perform(
            get("/v1/api/variant/$variantId/$language")
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(variantService)
    }

    @Test
    fun getVariant_pathIsValid_variantIsReturned() {
        val language = Language.En

        val variantDTO = getVariantDTO()
        BDDMockito.given(variantService.getVariant(variantDTO.id, Language.En)).willReturn(variantDTO)

        val actualResponse = mockMvc.perform(
            get("/v1/api/variant/${variantDTO.id}/$language")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(variantDTO))
        verify(variantService).getVariant(variantDTO.id, Language.En)
    }

    @Test
    fun updateVariantStock_stockDeltaIsNull_badRequestIsReturned() {
        val request = UpdateStockRequest(stockDelta = null)

        val variantId = ObjectId().toString()
        mockMvc.perform(
            put("/v1/api/variant/$variantId/stock").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(variantService)
    }

    @Test
    fun updateVariantStock_requestIsValid_stockIsUpdated() {
        val request = UpdateStockRequest(stockDelta = 10)

        val variantId = ObjectId().toString()
        mockMvc.perform(
            put("/v1/api/variant/$variantId/stock").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(variantService).updateStock(variantId, 10)
    }

    private fun getVariantDTO(): VariantDTO {
        return VariantDTO(
            id = ObjectId().toString(),
            barcode = "1231245",
            productId = ObjectId().toString(),
            contentId = ObjectId().toString(),
            medias = listOf(VariantMedia(url = "media.url", 1)),
            attributes = emptyList(),
            creationDate = Instant.now(),
            modifiedDate = Instant.now(),
            prices = mapOf(Pair(Currency.UAH, PriceDTO(BigDecimal.TEN, BigDecimal.ONE))),
            stock = StockDTO(10)
        )
    }
}
