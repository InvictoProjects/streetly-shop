package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import org.bson.types.ObjectId

interface ContentRepository {
    fun save(content: Content): Content
    fun findByIdAggregated(id: ObjectId, language: Language): Content?
    fun getByIdAggregated(id: ObjectId, language: Language): Content
    fun existsById(id: ObjectId, language: Language): Boolean
    fun findById(id: ObjectId, language: Language): Content?
    fun getById(id: ObjectId, language: Language): Content
    fun getCreator(id: ObjectId, language: Language): ObjectId
}
