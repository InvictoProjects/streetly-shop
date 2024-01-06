package com.invictoprojects.streetlyshop.persistence.impl

import com.invictoprojects.streetlyshop.persistence.ContentRepository
import com.invictoprojects.streetlyshop.persistence.PREFIX
import com.invictoprojects.streetlyshop.persistence.SUFFIX
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import com.invictoprojects.streetlyshop.web.exception.ProductNotFoundException
import org.apache.commons.text.StringSubstitutor
import org.bson.codecs.BsonArrayCodec
import org.bson.codecs.DecoderContext
import org.bson.json.JsonReader
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DefaultContentRepository(
        @Value("\${mongodb.collection.contents}")
        val contentsPrefix: String,
        @Value("\${mongodb.collection.attribute-definitions}")
        val attributeDefinitionsPrefix: String,
        @Value("\${mongodb.collection.attribute-values}")
        val attributeValuesPrefix: String,
        @Qualifier("contentAggregation")
        val contentAggregation: String,
        val mongoTemplate: MongoTemplate
) : ContentRepository {
    override fun save(content: Content): Content {
        return mongoTemplate.save(content, getCollection(contentsPrefix, content.languageCode))
    }

    override fun findByIdAggregated(id: ObjectId, language: Language): Content? {
        val params = mutableMapOf(
                Pair("contentId", id.toString()),
                Pair("attributeDefinitionsCollection", getCollection(attributeDefinitionsPrefix, language)),
                Pair("attributeValuesCollection", getCollection(attributeValuesPrefix, language))
        )

        val aggregation = StringSubstitutor.replace(contentAggregation, params,
                PREFIX,
                SUFFIX
        )

        val pipeline = BsonArrayCodec()
                .decode(JsonReader(aggregation), DecoderContext.builder().build()).values
                .map { it.asDocument() }

        return mongoTemplate.execute(getCollection(contentsPrefix, language)) { collection ->
            collection
                    .aggregate(pipeline)
                    .map { mongoTemplate.converter.read(Content::class.java, it) }
                    .first()
        }
    }

    override fun getByIdAggregated(id: ObjectId, language: Language): Content {
        return findByIdAggregated(id, language)
                ?: throw ProductNotFoundException("Product with content id $id was not found")
    }

    override fun existsById(id: ObjectId, language: Language): Boolean {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists(query, getCollection(contentsPrefix, language))
    }

    override fun findById(id: ObjectId, language: Language): Content? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, Content::class.java, getCollection(contentsPrefix, language))
    }

    override fun getById(id: ObjectId, language: Language): Content {
        return findById(id, language) ?: throw ProductNotFoundException("Product with content id $id was not found")
    }

    override fun getCreator(id: ObjectId, language: Language): ObjectId {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, Content::class.java, getCollection(contentsPrefix, language))?.createdBy
                ?: throw ProductNotFoundException("Content with id $id was not found")
    }
}
