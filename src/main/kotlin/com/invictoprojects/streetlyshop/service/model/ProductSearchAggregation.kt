package com.invictoprojects.streetlyshop.service.model

import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductSortingOrder
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import org.bson.types.Decimal128
import org.bson.types.ObjectId
import java.time.Instant

data class ProductSearchAggregation(
    val query: String?,
    val createdBy: ObjectId?,
    val categoryId: ObjectId?,
    val productStatuses: List<ProductStatus>,
    val creationDateGT: Instant?,
    val creationDateLT: Instant?,
    val modifiedDateGT: Instant?,
    val modifiedDateLT: Instant?,
    val currency: Currency,
    val salePriceGT: Decimal128?,
    val salePriceLT: Decimal128?,
    val originalPriceGT: Decimal128?,
    val originalPriceLT: Decimal128?,
    val stockQuantityGT: Long,
    val stockQuantityLT: Long?,
    val productSortingOrder: ProductSortingOrder,
    val attributeValueFilter: List<List<ObjectId>>?,
    val language: Language,
    val pageSize: Long,
    val page: Long
)
