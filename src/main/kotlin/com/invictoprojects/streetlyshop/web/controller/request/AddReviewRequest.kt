package com.invictoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class AddReviewRequest(
    @field:NotBlank
    val variantId: String? = null,
    val mediaIds: List<String> = listOf(),
    @field:NotBlank
    val text: String? = null,
    @field:NotNull
    @field:Min(1)
    @field:Max(5)
    val rating: Int? = null
)
