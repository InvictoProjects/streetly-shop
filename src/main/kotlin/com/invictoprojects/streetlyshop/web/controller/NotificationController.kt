package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.TelegramService
import com.invictoprojects.streetlyshop.web.controller.request.CallBackRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Api("Notification Controller")
@Validated
@RestController
@RequestMapping("/v1/api/notification")
class NotificationController(
    val telegramService: TelegramService
) {

    @ApiOperation("Leave contacts to call back")
    @PostMapping("callback")
    fun callBack(@Valid @RequestBody callBackRequest: CallBackRequest) {
        telegramService.notifyTeam(callBackRequest.getNotificationMessage())
    }
}
