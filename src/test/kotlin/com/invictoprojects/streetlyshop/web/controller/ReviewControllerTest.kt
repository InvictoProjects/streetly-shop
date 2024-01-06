package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.service.ReviewService
import com.invictoprojects.streetlyshop.web.controller.dto.ReviewDTO
import com.invictoprojects.streetlyshop.web.controller.request.AddReviewRequest
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class ReviewControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var reviewService: ReviewService

    @InjectMocks
    lateinit var controller: ReviewController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun addReview_variantIdIsBlank_badRequestIsReturned() {
        val request = AddReviewRequest(
            variantId = " ",
            text = "Cool T-Shirt",
            rating = 5
        )

        mockMvc.perform(
            post("/v1/api/review").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(reviewService)
    }

    @Test
    fun addReview_textIsBlank_badRequestIsReturned() {
        val request = AddReviewRequest(
            variantId = ObjectId().toString(),
            text = " ",
            rating = 5
        )

        mockMvc.perform(
            post("/v1/api/review").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(reviewService)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, 0, 6])
    fun addReview_ratingIsInvalid_badRequestIsReturned(rating: Int) {
        val request = AddReviewRequest(
            variantId = ObjectId().toString(),
            text = "Nice",
            rating = rating
        )

        mockMvc.perform(
            post("/v1/api/review").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(reviewService)
    }

    @Test
    fun addReview_requestIsValid_reviewDTOIsReturned() {
        val request = AddReviewRequest(
            variantId = ObjectId().toString(),
            text = "Cool T-Shirt",
            rating = 5
        )

        val review = ReviewDTO(
            id = ObjectId().toString(),
            productId = ObjectId().toString(),
            contentId = ObjectId().toString(),
            variantId = request.variantId!!,
            createdBy = ObjectId().toString(),
            customerName = "Vlad",
            customerAvatar = "vlad.avatar",
            creationDate = Instant.now(),
            medias = mutableListOf(),
            text = "Cool T-Shirt",
            rating = 5
        )

        given(reviewService.addReview(request)).willReturn(review)

        val actualResponse = mockMvc.perform(
            post("/v1/api/review/")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(review))
    }
}
