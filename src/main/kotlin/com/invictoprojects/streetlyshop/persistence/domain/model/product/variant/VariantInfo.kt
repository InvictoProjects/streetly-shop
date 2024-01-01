package com.invictoprojects.streetlyshop.persistence.domain.model.product.variant

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.Attribute
import org.bson.types.ObjectId
import java.time.Instant

data class VariantInfo(
    val contentId: ObjectId,
    var productId: ObjectId,
    val product: Product,
    var name: String = "",
    var description: String = "",
    var attributes: MutableList<Attribute> = mutableListOf(),
    var variantIds: MutableList<ObjectId> = mutableListOf(),
    var variants: Variant,
    var languageCode: Language,
    val creationDate: Instant = Instant.now(),
    var modifiedDate: Instant = Instant.now(),
    val createdBy: ObjectId
)
