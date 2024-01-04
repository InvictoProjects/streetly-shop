package com.invictoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.NotBlank

data class CreateCategoryRequest(
    val parentCategoryId: String? = null,
    @field:NotBlank
    val name: String? = null
)
