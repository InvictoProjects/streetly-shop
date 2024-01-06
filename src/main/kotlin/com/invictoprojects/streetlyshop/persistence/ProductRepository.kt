package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.PaginatedProductSearch
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.service.model.ProductSearchAggregation
import org.bson.types.ObjectId

interface ProductRepository {
    fun save(product: Product): Product
    fun findByIdAggregated(id: ObjectId, language: Language): Product?
    fun getByIdAggregated(id: ObjectId, language: Language): Product
    fun findById(id: ObjectId): Product?
    fun getById(id: ObjectId): Product
    fun increaseFavoriteCount(id: ObjectId)
    fun decreaseFavoriteCount(id: ObjectId)
    fun search(aggregation: ProductSearchAggregation): PaginatedProductSearch
}
