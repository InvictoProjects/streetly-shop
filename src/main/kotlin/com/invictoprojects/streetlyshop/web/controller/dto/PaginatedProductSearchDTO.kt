package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.PaginatedProductSearch
import com.invictoprojects.streetlyshop.service.toDTO

data class PaginatedProductSearchDTO(
    val paginatedResults: MutableList<VariantInfoDTO>,
    val totalCount: Long
)

fun PaginatedProductSearch.toDTO(): PaginatedProductSearchDTO {
    return PaginatedProductSearchDTO(
        paginatedResults = paginatedResults.map { it.toDTO() }.toMutableList(),
        totalCount = totalCount
    )
}
