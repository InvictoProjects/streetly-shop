package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.Attribute

data class AttributeDTO(
    var id: String,
    var valueId: String,
    var definition: AttributeDefinitionDTO? = null,
    var value: AttributeValueDTO? = null
)

fun Attribute.toDTO(): AttributeDTO {
    return AttributeDTO(
        id = id.toString(),
        valueId = valueId.toString(),
        definition = definition?.toDTO(),
        value = value?.toDTO()
    )
}
