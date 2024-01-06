package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UpdateAttributeValueNameRequest(
        @field:NotBlank
        val name: String?,
        @field:NotNull
        val language: Language?
)
