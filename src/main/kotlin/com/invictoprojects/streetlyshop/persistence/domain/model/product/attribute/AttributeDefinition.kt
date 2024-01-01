package com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class AttributeDefinition(
    @field:Id
    val id: ObjectId,
    var name: String,
    var starred: Boolean = false,
    var searchable: Boolean = false,
    var priority: Long = 1,
    var languageCode: Language,
    var valueIds: List<ObjectId>,
    var values: List<AttributeValue>? = null
)
