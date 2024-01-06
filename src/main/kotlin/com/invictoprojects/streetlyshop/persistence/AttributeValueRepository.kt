package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import org.bson.types.ObjectId

interface AttributeValueRepository {
    fun save(attributeValue: AttributeValue): AttributeValue
    fun findById(id: ObjectId, language: Language): AttributeValue?
    fun getById(id: ObjectId, language: Language = Language.En): AttributeValue
}
