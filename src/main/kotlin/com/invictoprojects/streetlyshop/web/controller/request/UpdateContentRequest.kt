package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class UpdateContentRequest(
        val name: String? = null,
        val description: String? = null,
        @field:Size(min = 1)
        val attributes: List<AttributeDTO>? = null,

        @field:NotNull
        val language: Language? = null
)
