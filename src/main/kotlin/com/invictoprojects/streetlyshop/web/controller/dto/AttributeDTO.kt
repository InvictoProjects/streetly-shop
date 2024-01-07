package com.invictoprojects.streetlyshop.web.controller.dto

data class AttributeDTO(
    var id: String,
    var valueId: String,
    var definition: AttributeDefinitionDTO? = null,
    var value: AttributeValueDTO? = null
)
