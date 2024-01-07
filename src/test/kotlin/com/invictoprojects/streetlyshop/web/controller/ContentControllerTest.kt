package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.ContentService
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.controller.dto.ContentDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateContentRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateContentRequest
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class ContentControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var contentService: ContentService

    @InjectMocks
    lateinit var controller: ContentController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun createContent_productIdIsBlank_badRequestIsReturned() {
        val request = CreateContentRequest(
            productId = " ",
            name = "T-Shirt",
            description = "Black T-Shirt",
            attributes = listOf(
                AttributeDTO(ObjectId().toString(), ObjectId().toString())
            )
        )

        mockMvc.perform(
            post("/v1/api/content").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(contentService)
    }

    @Test
    fun createContent_nameIsBlank_badRequestIsReturned() {
        val request = CreateContentRequest(
            productId = ObjectId().toString(),
            name = " ",
            description = "Black T-Shirt",
            attributes = listOf(AttributeDTO(ObjectId().toString(), ObjectId().toString()))
        )

        mockMvc.perform(
            post("/v1/api/content").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(contentService)
    }

    @Test
    fun createContent_descriptionIsBlank_badRequestIsReturned() {
        val request = CreateContentRequest(
            productId = ObjectId().toString(),
            name = "T-Shirt",
            description = " ",
            attributes = listOf(AttributeDTO(ObjectId().toString(), ObjectId().toString()))
        )

        mockMvc.perform(
            post("/v1/api/content").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(contentService)
    }

    @Test
    fun createContent_requestIsValid_contentIsCreated() {
        val request = CreateContentRequest(
            productId = ObjectId().toString(),
            name = "T-Shirt",
            description = "Black T-Shirt",
            attributes = listOf(AttributeDTO(ObjectId().toString(), ObjectId().toString()))
        )

        mockMvc.perform(
            post("/v1/api/content").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(contentService).createContent(request)
    }

    @Test
    fun updateContent_languageIsNull_badRequestIsReturned() {
        val contentId = ObjectId().toString()
        val request = UpdateContentRequest(name = "Red Dress", language = null)

        mockMvc.perform(
            put("/v1/api/content/$contentId").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(contentService)
    }

    @Test
    fun updateContent_attributeListIsEmpty_badRequestIsReturned() {
        val contentId = ObjectId().toString()
        val request = UpdateContentRequest(name = "Red Dress", attributes = listOf(), language = Language.En)

        mockMvc.perform(
            put("/v1/api/content/$contentId").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(contentService)
    }

    @Test
    fun updateContent_requestIsValid_contentIsUpdated() {
        val request = UpdateContentRequest(name = "Red Dress", language = Language.En)

        val contentDTO = getContentDTO()
        BDDMockito.given(contentService.updateContent(contentDTO.id, request)).willReturn(contentDTO)

        val actualResponse = mockMvc.perform(
            put("/v1/api/content/${contentDTO.id}")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(contentDTO))
        verify(contentService).updateContent(contentDTO.id, request)
    }

    @Test
    fun getContent_languageIsInvalid_badRequestIsReturned() {
        val contentId = ObjectId().toString()
        val language = "invalid"

        mockMvc.perform(
            get("/v1/api/content/$contentId/$language")
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(contentService)
    }

    @Test
    fun getContent_pathIsValid_contentIsReturned() {
        val language = Language.En

        val contentDTO = getContentDTO()
        BDDMockito.given(contentService.getContent(contentDTO.id, Language.En)).willReturn(contentDTO)

        val actualResponse = mockMvc.perform(
            get("/v1/api/content/${contentDTO.id}/$language")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(contentDTO))
        verify(contentService).getContent(contentDTO.id, Language.En)
    }

    private fun getContentDTO(): ContentDTO {
        return ContentDTO(
            id = ObjectId().toString(),
            productId = ObjectId().toString(),
            name = "T-Shirt",
            description = "White T-Shirt",
            variantIds = emptyList(),
            variants = emptyList(),
            attributes = emptyList(),
            creationDate = Instant.now(),
            modifiedDate = Instant.now()
        )
    }
}
