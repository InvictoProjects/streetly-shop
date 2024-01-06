package com.invictoprojects.streetlyshop.persistence.impl

import com.invictoprojects.streetlyshop.persistence.AttributeDefinitionRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeDefinition
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.web.exception.AttributeNotFoundException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DefaultAttributeDefinitionRepository(
    @Value("\${mongodb.collection.attribute-definitions}")
    val attributeDefinitionsPrefix: String,
    @Value("\${mongodb.collection.attribute-values}")
    val attributeValuesPrefix: String,
    val mongoTemplate: MongoTemplate
) : AttributeDefinitionRepository {
    override fun save(attributeDefinition: AttributeDefinition): AttributeDefinition {
        return mongoTemplate.save(
            attributeDefinition,
            getCollection(attributeDefinitionsPrefix, attributeDefinition.languageCode)
        )
    }

    override fun findById(id: ObjectId, language: Language): AttributeDefinition? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        val collection = getCollection(attributeDefinitionsPrefix, language)
        return mongoTemplate.findOne(query, AttributeDefinition::class.java, collection)
    }

    override fun getById(id: ObjectId, language: Language): AttributeDefinition {
        return findById(id, language) ?: throw AttributeNotFoundException("Attribute with id $id was not found")
    }

    override fun getByIdAggregated(id: ObjectId, language: Language): AttributeDefinition {
        val matchStage = Aggregation.match(Criteria.where("_id").isEqualTo(id))
        val attributeValuesCollection = getCollection(attributeValuesPrefix, language)
        val lookupStage = Aggregation.lookup(attributeValuesCollection, "valueIds", "_id", "values")

        val aggregation = Aggregation.newAggregation(matchStage, lookupStage)
        val attributeDefinitionsCollection = getCollection(attributeDefinitionsPrefix, language)
        return mongoTemplate.aggregate(aggregation, attributeDefinitionsCollection, AttributeDefinition::class.java)
            .single()
    }
}
