package com.invictoprojects.streetlyshop.persistence.impl

import com.invictoprojects.streetlyshop.persistence.AttributeValueRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.web.exception.AttributeValueNotFoundException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DefaultAttributeValueRepository(
        @Value("\${mongodb.collection.attribute-values}")
        val attributeValuesPrefix: String,
        val mongoTemplate: MongoTemplate
) : AttributeValueRepository {

    override fun save(attributeValue: AttributeValue): AttributeValue {
        val collection = getCollection(attributeValuesPrefix, attributeValue.languageCode)
        return mongoTemplate.save(attributeValue, collection)
    }

    override fun findById(id: ObjectId, language: Language): AttributeValue? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        val collection = getCollection(attributeValuesPrefix, language)
        return mongoTemplate.findOne(query, AttributeValue::class.java, collection)
    }

    override fun getById(id: ObjectId, language: Language): AttributeValue {
        return findById(id, language)
                ?: throw AttributeValueNotFoundException("Attribute value with id $id was not found")
    }
}
