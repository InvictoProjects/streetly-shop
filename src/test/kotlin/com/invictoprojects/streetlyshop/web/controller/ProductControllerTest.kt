package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import com.invictoprojects.streetlyshop.service.ProductService
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.controller.dto.CategoryDTO
import com.invictoprojects.streetlyshop.web.controller.dto.ProductDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateProductRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateProductRequest
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
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
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class ProductControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var productService: ProductService

    @InjectMocks
    lateinit var controller: ProductController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun createProduct_categoryIdIsBlank_badRequestIsReturned() {
        val request = CreateProductRequest(
            categoryId = " ",
            attributes = listOf(AttributeDTO(ObjectId().toString(), ObjectId().toString()))
        )

        mockMvc.perform(
            post("/v1/api/product").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(productService)
    }

    @Test
    fun createProduct_requestIsValid_productIsCreated() {
        val request = CreateProductRequest(
            categoryId = ObjectId().toString(),
            attributes = listOf(AttributeDTO(ObjectId().toString(), ObjectId().toString()))
        )

        mockMvc.perform(
            post("/v1/api/product").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(productService).createProduct(request)
    }

    @Test
    fun updateProduct_requestIsValid_productIsUpdated() {
        val request = UpdateProductRequest(categoryId = ObjectId().toString(), attributes = null, productStatus = null)
        val productId = ObjectId()

        val productDTO = getProductDTO()
        given(productService.updateProduct(productId.toString(), request)).willReturn(productDTO)

        val actualResponse = mockMvc.perform(
            put("/v1/api/product/$productId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(productDTO))
    }

    @Test
    fun getProduct_languageIsInvalid_badRequestIsReturned() {
        val productId = ObjectId().toString()
        val language = "invalid"

        mockMvc.perform(
            get("/v1/api/product/$productId/$language")
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(productService)
    }

    @Test
    fun getProduct_pathIsValid_productIsReturned() {
        val language = Language.En

        val productDTO = getProductDTO()
        given(productService.getProduct(productDTO.id, Language.En)).willReturn(productDTO)

        val actualResponse = mockMvc.perform(
            get("/v1/api/product/${productDTO.id}/$language")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(productDTO))
        verify(productService).getProduct(productDTO.id, Language.En)
    }

    private fun getProductDTO(): ProductDTO {
        val categoryId = ObjectId().toString()
        return ProductDTO(
            id = ObjectId().toString(),
            categoryId = categoryId,
            category = CategoryDTO(
                id = categoryId,
                name = "Clothes",
                creationDate = Instant.now(),
                modifiedDate = Instant.now()
            ),
            creationDate = Instant.now(),
            modifiedDate = Instant.now(),
            contentIds = emptyList(),
            contents = emptyList(),
            attributes = emptyList(),
            status = ProductStatus.DRAFT,
            rating = 0.0,
            reviewCount = 0,
            reviewIds = listOf(),
            reviews = listOf(),
            favoriteCount = 4
        )
    }
}
