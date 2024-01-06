package com.invictoprojects.streetlyshop.web.controller.dto

data class AttributeDefinitionDTO(
    var id: String,
    var name: String,
    var starred: Boolean,
    var searchable: Boolean,
    var priority: Long,
    var values: List<AttributeValueDTO>?
)
