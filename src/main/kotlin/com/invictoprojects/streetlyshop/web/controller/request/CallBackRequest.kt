package com.invictoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.NotBlank

data class CallBackRequest(
    @field:NotBlank val name: String?,
    @field:NotBlank val phone: String?
) {
    fun getNotificationMessage(): String {
        return "\uD83D\uDD25 Customer $name wants us to call back: $phone"
    }
}
