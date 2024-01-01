package com.invictoprojects.streetlyshop.persistence.domain.model.product.variant

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.Attribute
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Variant(
    @field:Id
    val id: ObjectId,
    var barcode: String,
    var productId: ObjectId,
    var contentId: ObjectId,
    var attributes: MutableList<Attribute> = mutableListOf(),
    var medias: MutableList<VariantMedia> = mutableListOf(),
    val createdBy: ObjectId,
    val creationDate: Instant = Instant.now(),
    var modifiedDate: Instant = Instant.now(),
    var stock: Stock = Stock()
)
