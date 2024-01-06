package com.invictoprojects.streetlyshop.service.model

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import org.bson.types.ObjectId

data class AttributeSearchAggregation(
    val createdBy: ObjectId?,
    val categoryId: ObjectId?,
    val productStatuses: List<ProductStatus>,
    val language: Language
)
