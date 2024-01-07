package com.invictoprojects.streetlyshop.web.controller.dto

data class PaginatedProductSearchDTO(
    val paginatedResults: MutableList<VariantInfoDTO>,
    val totalCount: Long
)
