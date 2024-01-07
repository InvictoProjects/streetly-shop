package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.AttributeValueService
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeValueNameRequest
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
internal class AttributeValueControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var attributeValueService: AttributeValueService

    @InjectMocks
    lateinit var controller: AttributeValueController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun updateAttributeValue_nameIsBlank_badRequestIsReturned() {
        val request = UpdateAttributeValueNameRequest(name = " ", language = Language.En)

        val attributeValueId = ObjectId().toString()
        mockMvc.perform(
            put("/v1/api/attribute/value/$attributeValueId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(attributeValueService)
    }

    @Test
    fun updateAttributeValue_languageIsNull_badRequestIsReturned() {
        val request = UpdateAttributeValueNameRequest(name = "S", language = null)

        val attributeValueId = ObjectId().toString()
        mockMvc.perform(
            put("/v1/api/attribute/value/$attributeValueId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(attributeValueService)
    }

    @Test
    fun updateAttributeValue_requestIsValid_nameIsUpdated() {
        val request = UpdateAttributeValueNameRequest(name = "S", language = Language.En)
        val attributeValueId = ObjectId().toString()

        mockMvc.perform(
            put("/v1/api/attribute/value/$attributeValueId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        verify(attributeValueService).updateName(ObjectId(attributeValueId), request)
    }
}
