package com.invoctoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invoctoprojects.streetlyshop.service.LoginService
import com.invoctoprojects.streetlyshop.web.controller.request.BasicLoginRequest
import com.invoctoprojects.streetlyshop.web.controller.response.LoginResponse
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import javax.servlet.http.Cookie

@ExtendWith(MockitoExtension::class)
internal class AuthenticationControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var loginService: LoginService

    @InjectMocks
    lateinit var controller: AuthenticationController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
    }

    @Test
    fun basicLogin_emailIsInvalid_badRequestIsReturned() {
        val request = BasicLoginRequest("  ", "password")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/api/auth/basic-login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)

        Mockito.verify(loginService, Mockito.never()).login(request)
    }

    @Test
    fun basicLogin_passwordIsInvalid_badRequestIsReturned() {
        val request = BasicLoginRequest("email", "  ")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/api/auth/basic-login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)

        Mockito.verify(loginService, Mockito.never()).login(request)
    }

    @Test
    fun basicLogin_requestIsValid_tokenIsReturned() {
        val request = BasicLoginRequest("email", "password")
        val loginResponse = LoginResponse("accessToken")

        BDDMockito.given(loginService.login(request)).willReturn(ResponseEntity.status(HttpStatus.OK).body(loginResponse))

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/api/auth/basic-login")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn().response

        AssertionsForInterfaceTypes.assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        AssertionsForInterfaceTypes.assertThat(response.contentAsString).isEqualTo(objectMapper.writeValueAsString(loginResponse))
        Mockito.verify(loginService).login(request)
    }

    @Test
    fun refresh_cookieIsMissing_badRequestIsReturned() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/api/auth/refresh").param("userId", ObjectId().toString())
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun refresh_userIdIsMissing_badRequestIsReturned() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/api/auth/refresh").cookie(Cookie("refreshToken", "token"))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun refresh_requestIsValid_tokenIsReturned() {
        val userId = ObjectId()
        val refreshToken = "refresh_token"
        val loginResponse = LoginResponse("accessToken")

        BDDMockito.given(loginService.refresh(userId, refreshToken)).willReturn(loginResponse)

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/api/auth/refresh")
                .param("userId", userId.toString())
                .cookie(Cookie("refreshToken", refreshToken))
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn().response

        AssertionsForInterfaceTypes.assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        AssertionsForInterfaceTypes.assertThat(response.contentAsString).isEqualTo(objectMapper.writeValueAsString(loginResponse))
        Mockito.verify(loginService).refresh(userId, refreshToken)
    }
}
