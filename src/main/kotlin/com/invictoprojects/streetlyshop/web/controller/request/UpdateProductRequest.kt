package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO

data class UpdateProductRequest(
    val categoryId: String? = null,
    val attributes: List<AttributeDTO>? = null,
    val productStatus: ProductStatus? = null,
)
