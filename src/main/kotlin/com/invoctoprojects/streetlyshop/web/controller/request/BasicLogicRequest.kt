package com.invoctoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.NotBlank

data class BasicLoginRequest(
    @field:NotBlank val email: String?,
    @field:NotBlank val password: String?
)
