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

    companion object {
        private const val CHAT_ID = "123"
        private const val TOKEN = "test_token"
        private const val MESSAGE = "User John wants us to call back"
    }

    @BeforeEach
    fun setup() {
        telegramService = TelegramService(TOKEN, CHAT_ID, telegramClient)
    }

    @Test
    fun notifyTeam_messageIsValid_messageIsSent() {
        telegramService.notifyTeam(MESSAGE)

        verify(telegramClient).sendMessage(TOKEN, CHAT_ID, MESSAGE)
    }

    @Test
    fun notifyTeam_telegramReturnsError_exceptionIsThrown() {
        given(telegramClient.sendMessage(TOKEN, CHAT_ID, MESSAGE))
            .willThrow(RuntimeException())

        val throwable = catchThrowable { telegramService.notifyTeam(MESSAGE) }

        assertThat(throwable).isNotNull
    }
}
