package com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute

import org.bson.types.ObjectId

data class Attribute(
    val id: ObjectId,
    val valueId: ObjectId,
    var definition: AttributeDefinition? = null,
    var value: AttributeValue? = null
)
