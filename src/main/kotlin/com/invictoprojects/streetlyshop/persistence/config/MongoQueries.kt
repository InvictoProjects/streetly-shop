package com.invictoprojects.streetlyshop.persistence.config

import com.invictoprojects.streetlyshop.service.ResourceReader
import org.bson.BsonDocument
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MongoQueries {

    @Bean
    fun productAggregation(): String {
        return ResourceReader.readResource("aggregations/getProduct.tmp")
    }

    @Bean
    fun contentAggregation(): String {
        return ResourceReader.readResource("aggregations/getContent.tmp")
    }

    @Bean
    fun variantAggregation(): String {
        return ResourceReader.readResource("aggregations/getVariant.tmp")
    }

    @Bean
    fun variantPriceUpdateAggregation(): String {
        return ResourceReader.readResource("aggregations/variantPriceUpdate.tmp")
    }

    @Bean
    fun variantStockUpdateAggregation(): String {
        return ResourceReader.readResource("aggregations/variantStockUpdate.tmp")
    }

    @Bean
    fun productSearchPipeline(): String {
        return ResourceReader.readResource("aggregations/search/searchPipeline.tmp")
    }

    @Bean
    fun matchObjectIdStage(): String {
        return ResourceReader.readResource("aggregations/search/matchObjectIdStage.tmp")
    }

    @Bean
    fun matchInStage(): String {
        return ResourceReader.readResource("aggregations/search/matchInStage.tmp")
    }

    @Bean
    fun matchGTStage(): String {
        return ResourceReader.readResource("aggregations/search/matchGTStage.tmp")
    }

    @Bean
    fun matchLTStage(): String {
        return ResourceReader.readResource("aggregations/search/matchLTStage.tmp")
    }

    @Bean
    fun sortStage(): String {
        return ResourceReader.readResource("aggregations/search/sortStage.tmp")
    }

    @Bean
    fun facetStage(): String {
        return ResourceReader.readResource("aggregations/search/facetStage.tmp")
    }

    @Bean
    fun textSearchStage(): String {
        return ResourceReader.readResource("aggregations/search/textSearchStage.tmp")
    }

    @Bean
    fun projectStage(): String {
        return ResourceReader.readResource("aggregations/search/projectStage.tmp")
    }

    @Bean
    fun variantInfoAggregation(): String {
        return ResourceReader.readResource("aggregations/getVariantInfo.tmp")
    }

    @Bean
    fun searchableAttributesAggregation(): String {
        return ResourceReader.readResource("aggregations/search/searchableAttributesPipeline.tmp")
    }
}

fun String.toBson(): BsonDocument {
    return BsonDocument.parse(this)
}
