package com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute

import org.bson.types.ObjectId

data class AttributeSearch(
    val id: ObjectId,
    var definition: AttributeDefinition,
    var valueIds: List<ObjectId>,
    var values: List<AttributeValue>
)
