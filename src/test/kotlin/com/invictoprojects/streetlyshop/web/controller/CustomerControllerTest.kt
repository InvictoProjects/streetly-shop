package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Gender
import com.invictoprojects.streetlyshop.service.CustomerService
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.request.AddFavoriteProductRequest
import com.invictoprojects.streetlyshop.web.controller.request.RegisterRequest
import com.invictoprojects.streetlyshop.web.controller.request.RemoveFavoriteProductRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCustomerDetailsRequest
import com.invictoprojects.streetlyshop.web.controller.response.ImageUploadResponse
import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class CustomerControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var customerService: CustomerService

    @Mock
    lateinit var authenticationFacade: AuthenticationFacade

    @InjectMocks
    lateinit var controller: CustomerController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun register_emailIsBlank_badRequestIsReturned() {
        val request = RegisterRequest("  ", "password")

        mockMvc.perform(
            post("/v1/api/customer/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(customerService)
    }

    @Test
    fun register_emailIsInvalid_badRequestIsReturned() {
        val request = RegisterRequest("notmail", "password")

        mockMvc.perform(
            post("/v1/api/customer/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(customerService)
    }

    @Test
    fun register_passwordIsInvalid_badRequestIsReturned() {
        val request = RegisterRequest("john@gmail.com", " ")

        mockMvc.perform(
            post("/v1/api/customer/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(customerService)
    }

    @Test
    fun register_passwordIsTooShort_badRequestIsReturned() {
        val request = RegisterRequest("john@gmail.com", "1234567")

        mockMvc.perform(
            post("/v1/api/customer/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(customerService)
    }

    @Test
    fun register_requestIsValid_userIsRegistered() {
        val request = RegisterRequest("john@gmail.com", "password")

        mockMvc.perform(
            post("/v1/api/customer/register")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(customerService).register("john@gmail.com", "password")
    }

    @Test
    fun updateAvatar_fileIsValid_imageUploadResponseIsReturned() {
        val file = MockMultipartFile("file", "me.jpg", "image/jpg", "image".toByteArray())

        val userId = ObjectId()
        val authentication = UsernamePasswordAuthenticationToken(userId.toString(), "password")
        given(authenticationFacade.getAuthentication()).willReturn(authentication)
        given(customerService.updateAvatar(file, userId)).willReturn(ImageUploadResponse("url"))

        val response = mockMvc.perform(
            multipart("/v1/api/customer/avatar")
                .file(file)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(response.contentAsString).isEqualTo(objectMapper.writeValueAsString(ImageUploadResponse("url")))
        verify(authenticationFacade).getAuthentication()
        verify(customerService).updateAvatar(file, userId)
    }

    @Test
    fun updateDetails_requestIsValid_customerIsUpdated() {
        val request = UpdateCustomerDetailsRequest(
            phone = "380991234567",
            birthDay = Instant.now().toEpochMilli(),
            name = "Vlad",
            surname = "Smith",
            middleName = "Mr",
            gender = Gender.MALE,
            nickname = "Vlad"
        )

        val userId = ObjectId()
        given(authenticationFacade.getAuthentication()).willReturn(
            UsernamePasswordAuthenticationToken(
                userId.toString(),
                null
            )
        )

        mockMvc.perform(
            put("/v1/api/customer")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(customerService).updateDetails(userId, request)
    }

    @Test
    fun updateDetails_phoneIsInvalid_badRequestIsReturned() {
        val request = UpdateCustomerDetailsRequest(phone = "1234")

        mockMvc.perform(
            put("/v1/api/customer")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(customerService)
    }

    @Test
    fun addFavoriteProduct_productIdIsBlank_badRequestIsReturned() {
        val request = AddFavoriteProductRequest(productId = " ")

        mockMvc.perform(
            post("/v1/api/customer/favorite-product")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(customerService)
    }

    @Test
    fun addFavoriteProduct_productIdIsValid_favoriteProductIsAdded() {
        val request = AddFavoriteProductRequest(productId = ObjectId().toString())

        val userId = ObjectId().toString()
        given(authenticationFacade.getAuthentication()).willReturn(
            UsernamePasswordAuthenticationToken(
                userId,
                null
            )
        )

        mockMvc.perform(
            post("/v1/api/customer/favorite-product")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(customerService).addFavoriteProduct(userId, request.productId!!)
    }

    @Test
    fun removeFavoriteProduct_productIdIsBlank_badRequestIsReturned() {
        val request = RemoveFavoriteProductRequest(productId = " ")

        mockMvc.perform(
            delete("/v1/api/customer/favorite-product")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(customerService)
    }

    @Test
    fun removeFavoriteProduct_productIdIsValid_favoriteProductIsRemoved() {
        val request = RemoveFavoriteProductRequest(productId = ObjectId().toString())

        val userId = ObjectId().toString()
        given(authenticationFacade.getAuthentication()).willReturn(
            UsernamePasswordAuthenticationToken(
                userId,
                null
            )
        )

        mockMvc.perform(
            delete("/v1/api/customer/favorite-product")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(customerService).removeFavoriteProduct(userId, request.productId!!)
    }
}
