package com.invictoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.NotBlank

data class RemoveFavoriteProductRequest(
    @field:NotBlank
    val productId: String? = null
)
