package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import com.invictoprojects.streetlyshop.service.AttributeSearchService
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDefinitionDTO
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeSearchDTO
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeValueDTO
import com.invictoprojects.streetlyshop.web.controller.request.AttributeSearchRequest
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
internal class AttributeSearchControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var attributeSearchService: AttributeSearchService

    @InjectMocks
    lateinit var controller: AttributeSearchController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun searchAttributes_languageIsUa_foundAttributesAreReturned() {
        val request = AttributeSearchRequest(
            createdBy = ObjectId().toString(),
            categoryId = ObjectId().toString(),
            productStatuses = listOf(ProductStatus.ACTIVE),
            language = Language.Ua
        )

        val definition = AttributeDefinitionDTO(
            id = ObjectId().toString(),
            name = "Size",
            starred = true,
            searchable = true,
            priority = 1000,
            values = null
        )
        val attribute = AttributeSearchDTO(
            id = ObjectId().toString(),
            definition = definition,
            values = listOf(AttributeValueDTO(ObjectId().toString(), "S"))
        )

        given(attributeSearchService.search(request)).willReturn(listOf(attribute))

        val actualResponse = mockMvc.perform(
            post("/v1/api/attribute/search")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(listOf(attribute)))

        verify(attributeSearchService).search(request)
    }
}
