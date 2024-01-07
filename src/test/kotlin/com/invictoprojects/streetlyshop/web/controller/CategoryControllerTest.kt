package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.CategoryService
import com.invictoprojects.streetlyshop.web.controller.dto.CategoryDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateCategoryRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCategoryNameRequest
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
internal class CategoryControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var categoryService: CategoryService

    @InjectMocks
    lateinit var controller: CategoryController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun createCategory_nameIsBlank_badRequestIsReturned() {
        val request = CreateCategoryRequest(name = " ")

        mockMvc.perform(
            post("/v1/api/category").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(categoryService)
    }

    @Test
    fun createCategory_requestIsValid_contentIsCreated() {
        val request = CreateCategoryRequest(name = "Clothes")

        mockMvc.perform(
            post("/v1/api/category").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(categoryService).createCategory(request)
    }

    @Test
    fun getCategory_languageIsInvalid_badRequestIsReturned() {
        val categoryId = ObjectId().toString()
        val language = "invalid"

        mockMvc.perform(
            get("/v1/api/category/$categoryId/$language")
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(categoryService)
    }

    @Test
    fun getCategory_pathIsValid_categoryIsReturned() {
        val language = Language.En

        val categoryDTO = getCategoryDTO()
        BDDMockito.given(categoryService.getCategory(categoryDTO.id, Language.En)).willReturn(categoryDTO)

        val actualResponse = mockMvc.perform(
            get("/v1/api/category/${categoryDTO.id}/$language")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(categoryDTO))
        verify(categoryService).getCategory(categoryDTO.id, Language.En)
    }

    @Test
    fun updateName_nameIsBlank_badRequestIsReturned() {
        val request = UpdateCategoryNameRequest(name = " ", language = Language.En)

        val categoryId = ObjectId().toString()
        mockMvc.perform(
            put("/v1/api/category/$categoryId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(categoryService)
    }

    @Test
    fun updateName_languageIsNull_badRequestIsReturned() {
        val request = UpdateCategoryNameRequest(name = "Dresses", language = null)

        val categoryId = ObjectId().toString()
        mockMvc.perform(
            put("/v1/api/category/$categoryId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(categoryService)
    }

    @Test
    fun updateName_requestIsValid_nameIsUpdated() {
        val request = UpdateCategoryNameRequest(name = "Dresses", language = Language.En)
        val categoryId = ObjectId().toString()

        mockMvc.perform(
            put("/v1/api/category/$categoryId/name").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        verify(categoryService).updateName(categoryId, request)
    }

    private fun getCategoryDTO(): CategoryDTO {
        return CategoryDTO(
            id = ObjectId().toString(),
            name = "Clothes",
            creationDate = Instant.now(),
            modifiedDate = Instant.now()
        )
    }
}
