package com.invictoprojects.streetlyshop.persistence.impl

import com.invictoprojects.streetlyshop.persistence.CategoryRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Category
import com.invictoprojects.streetlyshop.web.exception.CategoryNotFoundException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DefaultCategoryRepository(
    @Value("\${mongodb.collection.categories}")
    val categoriesPrefix: String,
    val mongoTemplate: MongoTemplate
) : CategoryRepository {

    override fun save(category: Category): Category {
        val collection = getCollection(categoriesPrefix, category.languageCode)
        return mongoTemplate.save(category, collection)
    }

    override fun findById(id: ObjectId, language: Language): Category? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        val collection = getCollection(categoriesPrefix, language)
        return mongoTemplate.findOne(query, Category::class.java, collection)
    }

    override fun getById(id: ObjectId, language: Language): Category {
        return findById(id, language)
            ?: throw CategoryNotFoundException("Category with id $id was not found")
    }

    override fun existsById(id: ObjectId, language: Language): Boolean {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists(query, getCollection(categoriesPrefix, language))
    }
}
