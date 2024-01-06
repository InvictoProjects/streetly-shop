package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeDefinition
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import org.bson.types.ObjectId

interface AttributeDefinitionRepository {
    fun save(attributeDefinition: AttributeDefinition): AttributeDefinition
    fun findById(id: ObjectId, language: Language): AttributeDefinition?
    fun getById(id: ObjectId, language: Language): AttributeDefinition
    fun getByIdAggregated(id: ObjectId, language: Language): AttributeDefinition
}
