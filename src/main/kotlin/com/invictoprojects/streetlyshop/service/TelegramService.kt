package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.web.client.TelegramClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TelegramService(
    @Value("\${telegram.token}") val token: String,
    @Value("\${telegram.chatId}") val chatId: String,
    val telegramClient: TelegramClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun notifyTeam(message: String) {
        logger.info("Sending message to Telegram Monocode Team Group, message: '$message'")
        telegramClient.sendMessage(token, chatId, message)
    }
}
