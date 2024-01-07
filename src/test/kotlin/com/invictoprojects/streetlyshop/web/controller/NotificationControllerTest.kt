package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.service.TelegramService
import com.invictoprojects.streetlyshop.web.controller.request.CallBackRequest
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
internal class NotificationControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var telegramService: TelegramService

    @InjectMocks
    lateinit var controller: NotificationController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
    }

    @Test
    fun callBack_phoneIsBlank_badRequestIsReturned() {
        val request = CallBackRequest("John", " ")

        mockMvc.perform(
            post("/v1/api/notification/callback")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(telegramService)
    }

    @Test
    fun callBack_nameIsBlank_badRequestIsReturned() {
        val request = CallBackRequest(" ", "380991867712")

        mockMvc.perform(
            post("/v1/api/notification/callback")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest)

        verifyNoInteractions(telegramService)
    }

    @Test
    fun callBack_requestIsValid_telegramIsCalled() {
        val request = CallBackRequest("John", "380991867712")

        mockMvc.perform(
            post("/v1/api/notification/callback")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)

        verify(telegramService).notifyTeam(request.getNotificationMessage())
    }
}
