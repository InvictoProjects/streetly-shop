package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.Category
import java.time.Instant

data class CategoryDTO(
    var id: String,
    var parentCategoryId: String? = null,
    var name: String,
    var subcategoryIds: MutableList<String> = mutableListOf(),
    var creationDate: Instant,
    val modifiedDate: Instant
)

fun Category.toDTO(): CategoryDTO {
    return CategoryDTO(
        id = id.toString(),
        parentCategoryId = parentCategoryId?.toString(),
        name = name,
        subcategoryIds = subcategoryIds.map { it.toString() }.toMutableList(),
        creationDate = creationDate,
        modifiedDate = modifiedDate
    )
}
