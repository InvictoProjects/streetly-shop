package com.invictoprojects.streetlyshop.util

import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.model.ProductSearchAggregation
import com.invictoprojects.streetlyshop.web.controller.request.ProductSearchRequest

fun ProductSearchRequest.toAggregation(): ProductSearchAggregation {
    return ProductSearchAggregation(
        query = query,
        createdBy = createdBy?.toObjectId(),
        categoryId = categoryId?.toObjectId(),
        productStatuses = productStatuses,
        creationDateGT = creationDateGT,
        creationDateLT = creationDateLT,
        modifiedDateGT = modifiedDateGT,
        modifiedDateLT = modifiedDateLT,
        currency = currency,
        salePriceGT = salePriceGT?.toDecimal128(),
        salePriceLT = salePriceLT?.toDecimal128(),
        originalPriceGT = originalPriceGT?.toDecimal128(),
        originalPriceLT = originalPriceLT?.toDecimal128(),
        stockQuantityGT = stockQuantityGT,
        stockQuantityLT = stockQuantityLT,
        productSortingOrder = productSortingOrder,
        attributeValueFilter = attributeValueFilter?.map {
            it.map { attributeValueId ->
                attributeValueId.toObjectId()
            }
        },
        language = language,
        pageSize = pageSize,
        page = page
    )
}