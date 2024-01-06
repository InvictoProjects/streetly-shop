package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.AttributeDefinitionService
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDefinitionDTO
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeValueDTO
import com.invictoprojects.streetlyshop.web.controller.request.AddAttributeDefinitionRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeNameRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
internal class AttributeDefinitionControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var attributeDefinitionService: AttributeDefinitionService

    @InjectMocks
    lateinit var controller: AttributeDefinitionController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun createAttribute_nameIsBlank_badRequestIsReturned() {
        val request = AddAttributeDefinitionRequest(name = " ", values = listOf("S"))

        mockMvc.perform(
            post("/v1/api/attribute-definition").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(attributeDefinitionService)
    }

    @Test
    fun createAttribute_valueListIsEmpty_badRequestIsReturned() {
        val request = AddAttributeDefinitionRequest(name = "Size", values = listOf())

        mockMvc.perform(
            post("/v1/api/attribute-definition").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(attributeDefinitionService)
    }

    @Test
    fun createAttribute_requestIsValid_attributeIsCreated() {
        val request = AddAttributeDefinitionRequest(name = "Size", values = listOf("S", "M", "L"))
        val response = getAttributeDTO()
        given(attributeDefinitionService.addAttributeDefinition(request)).willReturn(response)

        val actualResponse = mockMvc.perform(
            post("/v1/api/attribute-definition").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        assertThat(actualResponse.contentAsString).isEqualTo(objectMapper.writeValueAsString(response))
        verify(attributeDefinitionService).addAttributeDefinition(request)
    }

    @Test
    fun updateAttributeName_nameIsBlank_badRequestIsReturned() {
        val request = UpdateAttributeNameRequest(name = " ", language = Language.En)

        val attributeId = ObjectId().toString()
        mockMvc.perform(
            put("/v1/api/attribute-definition/$attributeId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(attributeDefinitionService)
    }

    @Test
    fun updateAttributeName_languageIsNull_badRequestIsReturned() {
        val request = UpdateAttributeNameRequest(name = "Size", language = null)

        val attributeId = ObjectId().toString()
        mockMvc.perform(
            put("/v1/api/attribute-definition/$attributeId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(attributeDefinitionService)
    }

    @Test
    fun updateAttributeName_requestIsValid_nameIsUpdated() {
        val request = UpdateAttributeNameRequest(name = "Size", language = Language.En)
        val attributeId = ObjectId().toString()

        mockMvc.perform(
            put("/v1/api/attribute-definition/$attributeId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        verify(attributeDefinitionService).updateAttributeName(attributeId, request)
    }

    @Test
    fun getAttribute_languageIsInvalid_badRequestIsReturned() {
        val attributeId = ObjectId().toString()
        val language = "invalid"

        mockMvc.perform(
            get("/v1/api/attribute-definition/$attributeId/$language")
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(attributeDefinitionService)
    }

    @Test
    fun getAttribute_pathIsValid_attributeIsReturned() {
        val attributeId = ObjectId().toString()
        val language = Language.En

        val attributeDTO = getAttributeDTO()
        given(attributeDefinitionService.getAttributeDefinition(attributeId, Language.En)).willReturn(attributeDTO)

        val actualResponse = mockMvc.perform(
            get("/v1/api/attribute-definition/$attributeId/$language")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        assertThat(actualResponse.contentAsString).isEqualTo(objectMapper.writeValueAsString(attributeDTO))
        verify(attributeDefinitionService).getAttributeDefinition(attributeId, language)
    }

    private fun getAttributeDTO(): AttributeDefinitionDTO {
        val attributeValues = listOf(
            AttributeValueDTO(ObjectId().toString(), "S"),
            AttributeValueDTO(ObjectId().toString(), "M"),
            AttributeValueDTO(ObjectId().toString(), "L")
        )
        return AttributeDefinitionDTO(
            id = ObjectId().toString(),
            name = "Size",
            starred = false,
            searchable = false,
            priority = 1,
            values = attributeValues
        )
    }
}
