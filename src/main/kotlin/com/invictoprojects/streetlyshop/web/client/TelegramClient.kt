package com.invictoprojects.streetlyshop.web.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(value = "telegram", url = "https://api.telegram.org")
interface TelegramClient {

    @GetMapping("/{token}/sendMessage")
    fun sendMessage(
        @PathVariable("token") token: String,
        @RequestParam("chat_id") chatId: String,
        @RequestParam("text") message: String
    )
}