package com.invictoprojects.streetlyshop.persistence.domain.model.order

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo
import org.bson.types.ObjectId

data class OrderLine(
    val id: ObjectId,
    val variantInfo: VariantInfo,
    val quantity: Long
)
