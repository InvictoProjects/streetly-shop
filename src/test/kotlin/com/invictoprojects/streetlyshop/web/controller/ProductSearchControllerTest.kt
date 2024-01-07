package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.service.ProductSearchService
import com.invictoprojects.streetlyshop.web.controller.dto.PaginatedProductSearchDTO
import com.invictoprojects.streetlyshop.web.controller.request.ProductSearchRequest
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
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

@ExtendWith(MockitoExtension::class)
internal class ProductSearchControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var productSearchService: ProductSearchService

    @InjectMocks
    lateinit var controller: ProductSearchController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun searchProduct_pageSizeIsInvalid_badRequestIsReturned() {
        val request = ProductSearchRequest(
            pageSize = 0,
            page = 1
        )

        mockMvc.perform(
            post("/v1/api/product/search").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(productSearchService)
    }

    @Test
    fun searchProduct_pageIsInvalid_badRequestIsReturned() {
        val request = ProductSearchRequest(
            pageSize = 10,
            page = 0
        )

        mockMvc.perform(
            post("/v1/api/product/search").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(productSearchService)
    }

    @Test
    fun searchProduct_attributeValueFilterIsInvalid_badRequestIsReturned() {
        val request = ProductSearchRequest(
            attributeValueFilter = listOf(),
            pageSize = 10,
            page = 10
        )

        mockMvc.perform(
            post("/v1/api/product/search").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(productSearchService)
    }

    @Test
    fun searchProduct_requestIsValid_searchIsPerformed() {
        val request = ProductSearchRequest(
            attributeValueFilter = listOf(listOf(ObjectId().toString())),
            pageSize = 10,
            page = 10
        )

        val response = PaginatedProductSearchDTO(
            paginatedResults = mutableListOf(),
            totalCount = 0
        )

        given(productSearchService.search(request)).willReturn(response)

        val actualResponse = mockMvc.perform(
            post("/v1/api/product/search")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(response))

        verify(productSearchService).search(request)
    }
}
