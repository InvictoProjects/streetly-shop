package com.invictoprojects.streetlyshop.persistence.domain.model.product.content

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.Attribute
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Content(
    @field:Id
    val id: ObjectId,
    var productId: ObjectId,
    var name: String = "",
    var description: String = "",
    var attributes: MutableList<Attribute> = mutableListOf(),
    var variantIds: MutableList<ObjectId> = mutableListOf(),
    var variants: MutableList<Variant> = mutableListOf(),
    var languageCode: Language,
    val creationDate: Instant = Instant.now(),
    var modifiedDate: Instant = Instant.now(),
    val createdBy: ObjectId
) {
    fun addVariant(variantId: ObjectId): Content {
        variantIds.add(variantId)
        modifiedDate = Instant.now()
        return this
    }

}
