package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Category
import org.bson.types.ObjectId

interface CategoryRepository {
    fun save(category: Category): Category
    fun findById(id: ObjectId, language: Language): Category?
    fun getById(id: ObjectId, language: Language): Category
    fun existsById(id: ObjectId, language: Language): Boolean
}
