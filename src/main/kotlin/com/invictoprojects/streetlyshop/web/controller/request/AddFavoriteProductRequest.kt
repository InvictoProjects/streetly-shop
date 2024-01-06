package com.invictoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.NotBlank

data class AddFavoriteProductRequest(
    @field:NotBlank
    val productId: String? = null
)
