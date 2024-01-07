package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeSearch

data class AttributeSearchDTO(
    val id: String,
    var definition: AttributeDefinitionDTO,
    var values: List<AttributeValueDTO>
)

fun AttributeSearch.toDTO(): AttributeSearchDTO {
    return AttributeSearchDTO(
        id = id.toString(),
        definition = definition.toDTO(),
        values = values.map { it.toDTO() }
    )
}
