package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CreateProductRequest(
    @field:NotBlank
    val categoryId: String? = null,
    @field:Size(min = 1)
    val attributes: List<AttributeDTO> = emptyList()
)
