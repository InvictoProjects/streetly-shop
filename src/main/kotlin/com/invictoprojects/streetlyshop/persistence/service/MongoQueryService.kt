package com.invictoprojects.streetlyshop.persistence.service

import com.invictoprojects.streetlyshop.persistence.PREFIX
import com.invictoprojects.streetlyshop.persistence.SUFFIX
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductSortingOrder
import org.apache.commons.text.StringSubstitutor
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class MongoQueryService(

    @Qualifier("matchObjectIdStage")
    val matchObjectIdStage: String,
    @Qualifier("matchInStage")
    val matchInStage: String,
    @Qualifier("matchGTStage")
    val matchGTStage: String,
    @Qualifier("matchLTStage")
    val matchLTStage: String,
    @Qualifier("sortStage")
    val sortStage: String,
    @Qualifier("facetStage")
    val facetStage: String,
    @Qualifier("textSearchStage")
    val textSearchStage: String,
    @Qualifier("projectStage")
    val projectStage: String,
) {
    fun getMatchObjectIdStageJson(field: String, objectId: ObjectId): String {
        val properties = mutableMapOf<String, String>()
        properties["field"] = field
        properties["id"] = objectId.toString()

        return StringSubstitutor.replace(
            matchObjectIdStage,
            properties,
            PREFIX,
            SUFFIX
        )
    }

    fun getMatchInStageJson(field: String, list: List<String>): String {
        val properties = mutableMapOf<String, String>()
        properties["field"] = field
        properties["list"] = list.toString()

        return StringSubstitutor.replace(
            matchInStage,
            properties,
            PREFIX,
            SUFFIX
        )
    }

    fun getMatchGTStageJson(field: String, value: String): String {
        val properties = mutableMapOf<String, String>()
        properties["field"] = field
        properties["value"] = value

        return StringSubstitutor.replace(
            matchGTStage,
            properties,
            PREFIX,
            SUFFIX
        )
    }

    fun getMatchLTStageJson(field: String, value: String): String {
        val properties = mutableMapOf<String, String>()
        properties["field"] = field
        properties["value"] = value

        return StringSubstitutor.replace(
            matchLTStage,
            properties,
            PREFIX,
            SUFFIX
        )
    }

    fun getSortStageJson(productSortingOrder: ProductSortingOrder): String {
        val properties = mutableMapOf<String, String>()
        properties["field"] = productSortingOrder.field
        properties["direction"] = productSortingOrder.direction.toString()

        return StringSubstitutor.replace(
            sortStage,
            properties,
            PREFIX,
            SUFFIX
        )
    }

    fun getFacetStageJson(searchPipeline: String, pageSize: Long, page: Long): String {
        val properties = mutableMapOf<String, String>()
        properties["searchPipeline"] = searchPipeline
        properties["skip"] = (pageSize * (page - 1)).toString()
        properties["limit"] = pageSize.toString()

        return StringSubstitutor.replace(
            facetStage,
            properties,
            PREFIX,
            SUFFIX
        )
    }

    fun getTextSearchStageJson(query: String): String {
        return StringSubstitutor.replace(
            textSearchStage,
            mutableMapOf(Pair("query", query)),
            PREFIX,
            SUFFIX
        )
    }

    fun getProjectStageJson(): String {
        return projectStage
    }
}

