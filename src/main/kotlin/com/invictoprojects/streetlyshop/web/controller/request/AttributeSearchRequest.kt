package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus

data class AttributeSearchRequest(
    val createdBy: String? = null,
    val categoryId: String? = null,
    val productStatuses: List<ProductStatus> = listOf(ProductStatus.ACTIVE),
    val language: Language = Language.Ua
)
