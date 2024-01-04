package com.invictoprojects.streetlyshop.web.controller.dto

import java.time.Instant

data class CategoryDTO(
    var id: String,
    var parentCategoryId: String? = null,
    var name: String,
    var subcategoryIds: MutableList<String> = mutableListOf(),
    var creationDate: Instant,
    val modifiedDate: Instant
)
