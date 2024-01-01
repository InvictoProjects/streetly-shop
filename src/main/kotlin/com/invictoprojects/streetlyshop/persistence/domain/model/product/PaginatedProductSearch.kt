package com.invictoprojects.streetlyshop.persistence.domain.model.product

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo

data class PaginatedProductSearch(
    val paginatedResults: MutableList<VariantInfo> = mutableListOf(),
    val totalCount: Long = 0
)
