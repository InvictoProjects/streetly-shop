package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue

data class AttributeValueDTO(
    var id: String,
    var name: String
)

fun AttributeValue.toDTO(): AttributeValueDTO {
    return AttributeValueDTO(id.toString(), name)
}
