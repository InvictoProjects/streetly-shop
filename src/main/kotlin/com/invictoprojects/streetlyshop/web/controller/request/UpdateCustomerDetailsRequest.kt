package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Gender
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class UpdateCustomerDetailsRequest(
    @field:NotBlank
    @field:Pattern(regexp = "^\\d{10,12}\$")
    var phone: String? = null,

    var birthDay: Long? = null,
    var name: String? = null,
    var surname: String? = null,
    var middleName: String? = null,
    var gender: Gender? = null,
    var nickname: String? = null
)
