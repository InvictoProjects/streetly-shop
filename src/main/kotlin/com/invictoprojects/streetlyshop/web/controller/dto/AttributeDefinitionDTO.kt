package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeDefinition

data class AttributeDefinitionDTO(
    var id: String,
    var name: String,
    var starred: Boolean,
    var searchable: Boolean,
    var priority: Long,
    var values: List<AttributeValueDTO>?
)

fun AttributeDefinition.toDTO(): AttributeDefinitionDTO {
    return AttributeDefinitionDTO(id.toString(), name, starred, searchable, priority, values?.map { it.toDTO() })
}
