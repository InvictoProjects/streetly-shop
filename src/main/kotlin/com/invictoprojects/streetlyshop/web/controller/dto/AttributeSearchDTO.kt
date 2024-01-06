package com.invictoprojects.streetlyshop.web.controller.dto

data class AttributeSearchDTO(
    val id: String,
    var definition: AttributeDefinitionDTO,
    var values: List<AttributeValueDTO>
)
