package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CreateContentRequest(
    @field:NotBlank
    val productId: String? = null,
    @field:NotBlank
    val name: String? = null,
    @field:NotBlank
    val description: String? = null,
    @field:Size(min = 1)
    val attributes: List<AttributeDTO> = emptyList()
)
