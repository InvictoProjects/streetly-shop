package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.web.client.TelegramClient
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class TelegramServiceTest {

    @Mock
    lateinit var telegramClient: TelegramClient

    private lateinit var telegramService: TelegramService

    @BeforeEach
    fun setup() {
        telegramService = TelegramService("token", "123", telegramClient)
    }

    @Test
    fun notifyTeam_messageIsValid_messageIsSent() {
        telegramService.notifyTeam("User John wants us to call back")

        verify(telegramClient).sendMessage("token", "123", "User John wants us to call back")
    }

    @Test
    fun notifyTeam_telegramReturnsError_exceptionIsThrown() {
        given(telegramClient.sendMessage("token", "123", "User John wants us to call back"))
            .willThrow(RuntimeException())

        val throwable = catchThrowable { telegramService.notifyTeam("User John wants us to call back") }

        assertThat(throwable).isNotNull
    }
}
