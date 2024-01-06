package com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class AttributeValue(
    @field:Id
    val id: ObjectId,
    val attributeId: ObjectId,
    var name: String,
    var languageCode: Language
)
