package com.invictoprojects.streetlyshop.persistence.impl

import com.mongodb.client.model.Filters
import com.invictoprojects.streetlyshop.persistence.PREFIX
import com.invictoprojects.streetlyshop.persistence.SUFFIX
import com.invictoprojects.streetlyshop.persistence.VariantRepository
import com.invictoprojects.streetlyshop.persistence.config.toBson
import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo
import com.invictoprojects.streetlyshop.web.exception.ProductNotFoundException
import com.invictoprojects.streetlyshop.web.exception.StockException
import org.apache.commons.text.StringSubstitutor
import org.bson.BsonDocument
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
import java.math.BigDecimal

@Repository
class DefaultVariantRepository(
    @Value("\${mongodb.collection.variants}")
    val variantsCollection: String,
    @Value("\${mongodb.collection.products}")
    val productsCollection: String,
    @Value("\${mongodb.collection.contents}")
    val contentsPrefix: String,
    @Value("\${mongodb.collection.attribute-definitions}")
    val attributeDefinitionsPrefix: String,
    @Value("\${mongodb.collection.attribute-values}")
    val attributeValuesPrefix: String,
    @Qualifier("variantAggregation")
    val variantAggregation: String,
    @Qualifier("variantPriceUpdateAggregation")
    val priceUpdateAggregation: String,
    @Qualifier("variantStockUpdateAggregation")
    val stockUpdateAggregation: String,
    @Qualifier("variantInfoAggregation")
    val variantInfoAggregation: String,
    val mongoTemplate: MongoTemplate
) : VariantRepository {
    override fun save(variant: Variant): Variant {
        return mongoTemplate.save(variant, variantsCollection)
    }

    override fun findByIdAggregated(id: ObjectId, language: Language): Variant? {
        val params = mutableMapOf(
            Pair("variantId", id.toString()),
            Pair("attributeDefinitionsCollection", getCollection(attributeDefinitionsPrefix, language)),
            Pair("attributeValuesCollection", getCollection(attributeValuesPrefix, language))
        )

        val aggregation = StringSubstitutor.replace(
            variantAggregation,
            params,
            PREFIX,
            SUFFIX
        )

        val pipeline = BsonArrayCodec()
            .decode(JsonReader(aggregation), DecoderContext.builder().build()).values
            .map { it.asDocument() }

        return mongoTemplate.execute(variantsCollection) { collection ->
            collection
                .aggregate(pipeline)
                .map { mongoTemplate.converter.read(Variant::class.java, it) }
                .first()
        }
    }

    override fun getByIdAggregated(id: ObjectId, language: Language): Variant {
        return findByIdAggregated(id, language)
            ?: throw ProductNotFoundException("Product with variant id $id was not found")
    }

    override fun findById(id: ObjectId): Variant? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, Variant::class.java, variantsCollection)
    }

    override fun getById(id: ObjectId): Variant {
        return findById(id) ?: throw ProductNotFoundException("Variant with id $id was not found")
    }

    override fun updatePricesWithNewExchangeRate(currency: Currency, exchangeRate: BigDecimal) {
        val params = mutableMapOf(Pair("exchangeRate", exchangeRate.toString()), Pair("currency", currency.toString()))
        val priceAggregation = StringSubstitutor.replace(
            priceUpdateAggregation,
            params,
            PREFIX,
            SUFFIX
        )

        mongoTemplate.execute(variantsCollection) { collection ->
            collection.updateMany(BsonDocument(), mutableListOf(priceAggregation.toBson()))
        }
    }

    override fun updateStock(id: ObjectId, stockDelta: Long) {
        val params = mutableMapOf(Pair("stockDelta", stockDelta))
        val stockAggregation = StringSubstitutor.replace(
            stockUpdateAggregation,
            params,
            PREFIX,
            SUFFIX
        )

        return mongoTemplate.execute(variantsCollection) { collection ->
            val updateResult = collection.updateOne(Filters.eq("_id", id), mutableListOf(stockAggregation.toBson()))
            if (updateResult.modifiedCount == 0L) throw StockException("Variant with id $id has not enough stock")
        }
    }

    override fun findVariantInfoById(id: ObjectId, language: Language): VariantInfo? {
        val params = mutableMapOf(
            Pair("variantId", id.toString()),
            Pair("productsCollection", productsCollection),
            Pair("contentsCollection", getCollection(contentsPrefix, language)),
        )

        val aggregation = StringSubstitutor.replace(
            variantInfoAggregation,
            params,
            PREFIX,
            SUFFIX
        )

        val pipeline = BsonArrayCodec()
            .decode(JsonReader(aggregation), DecoderContext.builder().build()).values
            .map { it.asDocument() }

        return mongoTemplate.execute(variantsCollection) { collection ->
            collection
                .aggregate(pipeline)
                .map { mongoTemplate.converter.read(VariantInfo::class.java, it) }
                .first()
        }
    }

    override fun getVariantInfoById(id: ObjectId, language: Language): VariantInfo {
        return findVariantInfoById(id, language) ?: throw ProductNotFoundException("Variant with id $id was not found")
    }

    override fun getCreator(id: ObjectId): ObjectId {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, Variant::class.java, variantsCollection)?.createdBy
            ?: throw ProductNotFoundException("Variant with id $id was not found")
    }
}
