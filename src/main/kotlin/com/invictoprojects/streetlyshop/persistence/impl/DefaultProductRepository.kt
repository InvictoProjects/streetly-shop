package com.invictoprojects.streetlyshop.persistence.impl

import com.invictoprojects.streetlyshop.persistence.ContentRepository
import com.invictoprojects.streetlyshop.persistence.LIST_CLOSING_BRACKET
import com.invictoprojects.streetlyshop.persistence.LIST_OPENING_BRACKET
import com.invictoprojects.streetlyshop.persistence.PREFIX
import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.SUFFIX
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.PaginatedProductSearch
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeSearch
import com.invictoprojects.streetlyshop.persistence.service.MongoQueryService
import com.invictoprojects.streetlyshop.service.model.AttributeSearchAggregation
import com.invictoprojects.streetlyshop.service.model.ProductSearchAggregation
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
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DefaultProductRepository(
    @Value("\${mongodb.collection.products}")
    val productsCollection: String,
    @Value("\${mongodb.collection.contents}")
    val contentsPrefix: String,
    @Value("\${mongodb.collection.variants}")
    val variantsCollection: String,
    @Value("\${mongodb.collection.reviews}")
    val reviewsCollection: String,
    @Value("\${mongodb.collection.categories}")
    val categoriesPrefix: String,
    @Value("\${mongodb.collection.attribute-definitions}")
    val attributeDefinitionsPrefix: String,
    @Value("\${mongodb.collection.attribute-values}")
    val attributeValuesPrefix: String,

    @Qualifier("productAggregation")
    val productAggregation: String,
    @Qualifier("productSearchPipeline")
    val productSearchPipeline: String,
    @Qualifier("searchableAttributesAggregation")
    val searchableAttributesPipeline: String,

    val mongoQueryService: MongoQueryService,
    val contentRepository: ContentRepository,
    val mongoTemplate: MongoTemplate
) : ProductRepository {
    override fun save(product: Product): Product {
        return mongoTemplate.save(product, productsCollection)
    }

    override fun findByIdAggregated(id: ObjectId, language: Language): Product? {
        val params = mutableMapOf(
            Pair("productId", id.toString()),
            Pair("categoriesCollection", getCollection(categoriesPrefix, language)),
            Pair("reviewsCollection", reviewsCollection),
            Pair("attributeDefinitionsCollection", getCollection(attributeDefinitionsPrefix, language)),
            Pair("attributeValuesCollection", getCollection(attributeValuesPrefix, language))
        )

        val aggregation = StringSubstitutor.replace(
            productAggregation,
            params,
            PREFIX,
            SUFFIX
        )

        val pipeline = BsonArrayCodec()
            .decode(JsonReader(aggregation), DecoderContext.builder().build()).values
            .map { it.asDocument() }

        return mongoTemplate.execute(productsCollection) { collection ->
            collection
                .aggregate(pipeline)
                .map { mongoTemplate.converter.read(Product::class.java, it) }
                .map { product ->
                    val contents = product.contentIds.map { contentRepository.getByIdAggregated(it, language) }
                    product.addContents(contents)
                }
                .first()
        }
    }

    override fun getByIdAggregated(id: ObjectId, language: Language): Product {
        return findByIdAggregated(id, language) ?: throw ProductNotFoundException("Product with id $id was not found")
    }

    override fun findById(id: ObjectId): Product? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, Product::class.java, productsCollection)
    }

    override fun getById(id: ObjectId): Product {
        return findById(id) ?: throw ProductNotFoundException("Product with id $id was not found")
    }

    override fun increaseFavoriteCount(id: ObjectId) {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        mongoTemplate.updateFirst(query, Update().inc("favoriteCount", 1), productsCollection)
    }

    override fun decreaseFavoriteCount(id: ObjectId) {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        mongoTemplate.updateFirst(query, Update().inc("favoriteCount", -1), productsCollection)
    }

    override fun search(aggregation: ProductSearchAggregation): PaginatedProductSearch {
        with(aggregation) {
            val params = mutableMapOf<String, String>()
            params["productsCollection"] = productsCollection
            params["variantsCollection"] = variantsCollection
            params["productPropertiesSearchFilters"] = getProductPropertiesSearchFilters(aggregation).joinToString()
            params["variantPropertiesSearchFilters"] = getVariantPropertiesSearchFilters(aggregation).joinToString()
            params["attributesMatchStage"] = attributeValueFilter?.let { getMatchAttributeValueStage(it) } ?: ""
            params["sortStage"] = mongoQueryService.getSortStageJson(productSortingOrder)

            val searchPipelineFilters = StringSubstitutor.replace(productSearchPipeline, params, PREFIX, SUFFIX)

            val searchPipeline = mutableListOf<String>()

            query?.let { searchPipeline.add(mongoQueryService.getTextSearchStageJson(it)) }
            searchPipeline.add(mongoQueryService.getFacetStageJson(searchPipelineFilters, pageSize, page))
            searchPipeline.add(mongoQueryService.getProjectStageJson())

            val pipeline = BsonArrayCodec()
                .decode(
                    JsonReader("$LIST_OPENING_BRACKET${searchPipeline.joinToString()}$LIST_CLOSING_BRACKET"),
                    DecoderContext.builder().build()
                ).values
                .map { it.asDocument() }

            return mongoTemplate.execute(getCollection(contentsPrefix, language)) { collection ->
                collection
                    .aggregate(pipeline)
                    .map { mongoTemplate.converter.read(PaginatedProductSearch::class.java, it) }
                    .first()!!
            }
        }
    }

    private fun getProductPropertiesSearchFilters(aggregation: ProductSearchAggregation): MutableList<String> {
        with(aggregation) {
            val productPropertiesSearchFilters = mutableListOf<String>()

            createdBy?.let {
                val stage = mongoQueryService.getMatchObjectIdStageJson("product.createdBy", it)
                productPropertiesSearchFilters.add(stage)
            }

            categoryId?.let {
                val stage = mongoQueryService.getMatchObjectIdStageJson("product.categoryId", it)
                productPropertiesSearchFilters.add(stage)
            }

            val productStatusesMatchStage =
                mongoQueryService.getMatchInStageJson("product.status", productStatuses.map { "'$it'" })
            productPropertiesSearchFilters.add(productStatusesMatchStage)

            creationDateGT?.let {
                val stage = mongoQueryService.getMatchGTStageJson("product.creationDate", "new ISODate('$it')")
                productPropertiesSearchFilters.add(stage)
            }

            creationDateLT?.let {
                val stage = mongoQueryService.getMatchLTStageJson("product.creationDate", "new ISODate('$it')")
                productPropertiesSearchFilters.add(stage)
            }

            modifiedDateGT?.let {
                val stage = mongoQueryService.getMatchGTStageJson("product.modifiedDate", "new ISODate('$it')")
                productPropertiesSearchFilters.add(stage)
            }

            modifiedDateLT?.let {
                val stage = mongoQueryService.getMatchLTStageJson("product.modifiedDate", "new ISODate('$it')")
                productPropertiesSearchFilters.add(stage)
            }

            return productPropertiesSearchFilters
        }
    }

    private fun getVariantPropertiesSearchFilters(aggregation: ProductSearchAggregation): MutableList<String> {
        with(aggregation) {
            val variantPropertiesSearchFilters = mutableListOf<String>()

            salePriceGT?.let {
                val stage = mongoQueryService.getMatchGTStageJson(
                    "variants.prices.${currency}.salePrice",
                    "NumberDecimal('$it')"
                )
                variantPropertiesSearchFilters.add(stage)
            }

            salePriceLT?.let {
                val stage = mongoQueryService.getMatchLTStageJson(
                    "variants.prices.${currency}.salePrice",
                    "NumberDecimal('$it')"
                )
                variantPropertiesSearchFilters.add(stage)
            }

            originalPriceGT?.let {
                val stage = mongoQueryService.getMatchGTStageJson(
                    "variants.prices.${currency}.originalPrice",
                    "NumberDecimal('$it')"
                )
                variantPropertiesSearchFilters.add(stage)
            }

            originalPriceLT?.let {
                val stage = mongoQueryService.getMatchLTStageJson(
                    "variants.prices.${currency}.originalPrice",
                    "NumberDecimal('$it')"
                )
                variantPropertiesSearchFilters.add(stage)
            }

            stockQuantityGT.let {
                val stage = mongoQueryService.getMatchGTStageJson(
                    "variants.stock.quantity",
                    it.toString()
                )
                variantPropertiesSearchFilters.add(stage)
            }

            stockQuantityLT?.let {
                val stage = mongoQueryService.getMatchLTStageJson(
                    "variants.stock.quantity",
                    it.toString()
                )
                variantPropertiesSearchFilters.add(stage)
            }

            return variantPropertiesSearchFilters
        }
    }

    private fun getMatchAttributeValueStage(attributeValueFilter: List<List<ObjectId>>): String {
        val filters = attributeValueFilter.map {
            val orGroup =
                it.map { attributeValue ->
                    "{'allAttributes': {\$elemMatch: {'valueId': ObjectId('$attributeValue')}}}"
                }
            "{\$or: $orGroup}"
        }

        return "{\$match: {\$and: $filters}},"
    }

    override fun searchAttributes(aggregation: AttributeSearchAggregation): List<AttributeSearch> {
        val params = mutableMapOf<String, String>()
        params["contentsCollection"] = getCollection(contentsPrefix, aggregation.language)
        params["variantsCollection"] = variantsCollection
        params["attributeDefinitionsCollection"] = getCollection(attributeDefinitionsPrefix, aggregation.language)
        params["attributeValuesCollection"] = getCollection(attributeValuesPrefix, aggregation.language)
        params["productPropertiesSearchFilters"] = getProductPropertiesSearchFilters(aggregation).joinToString()

        val searchPipeline = StringSubstitutor.replace(
            searchableAttributesPipeline,
            params,
            PREFIX,
            SUFFIX
        )

        val pipeline = BsonArrayCodec()
            .decode(
                JsonReader(searchPipeline),
                DecoderContext.builder().build()
            ).values
            .map { it.asDocument() }

        return mongoTemplate.execute(productsCollection) { collection ->
            collection
                .aggregate(pipeline)
                .map { mongoTemplate.converter.read(AttributeSearch::class.java, it) }
                .toList()
        }
    }

    private fun getProductPropertiesSearchFilters(aggregation: AttributeSearchAggregation): MutableList<String> {
        with(aggregation) {
            val productPropertiesSearchFilters = mutableListOf<String>()

            createdBy?.let {
                val stage = mongoQueryService.getMatchObjectIdStageJson("createdBy", it)
                productPropertiesSearchFilters.add(stage)
            }

            categoryId?.let {
                val stage = mongoQueryService.getMatchObjectIdStageJson("categoryId", it)
                productPropertiesSearchFilters.add(stage)
            }

            val productStatusesMatchStage =
                mongoQueryService.getMatchInStageJson("status", productStatuses.map { "'$it'" })
            productPropertiesSearchFilters.add(productStatusesMatchStage)

            return productPropertiesSearchFilters
        }
    }
}
