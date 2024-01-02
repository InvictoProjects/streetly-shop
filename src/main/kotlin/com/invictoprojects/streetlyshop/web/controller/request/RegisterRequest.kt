package com.invictoprojects.streetlyshop.web.controller.request

import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class RegisterRequest(
    @field:Email
    @field:NotBlank
    val email: String?,

    @field:NotBlank
    @field:Length(min = 8)
    val password: String?
)
