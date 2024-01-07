package com.invictoprojects.streetlyshop.util

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.Attribute
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO

fun AttributeDTO.toAttribute(): Attribute {
    return Attribute(id = id.toObjectId(), valueId = valueId.toObjectId())
}
