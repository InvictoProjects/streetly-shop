package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.product.PaginatedProductSearch
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.model.ProductSearchAggregation
import com.invictoprojects.streetlyshop.util.toDTO
import com.invictoprojects.streetlyshop.util.toDecimal128
import com.invictoprojects.streetlyshop.web.controller.dto.PaginatedProductSearchDTO
import com.invictoprojects.streetlyshop.web.controller.dto.VariantInfoDTO
import com.invictoprojects.streetlyshop.web.controller.request.ProductSearchRequest
import org.springframework.stereotype.Service

@Service
class ProductSearchService(
    private val productRepository: ProductRepository
) {
    fun search(request: ProductSearchRequest): PaginatedProductSearchDTO {
        val searchAggregation = request.toAggregation()
        return productRepository.search(searchAggregation).toDTO()
    }
}

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

fun PaginatedProductSearch.toDTO(): PaginatedProductSearchDTO {
    return PaginatedProductSearchDTO(
        paginatedResults = paginatedResults.map { it.toDTO() }.toMutableList(),
        totalCount = totalCount
    )
}

fun VariantInfo.toDTO(): VariantInfoDTO {
    return VariantInfoDTO(
        contentId = contentId.toString(),
        productId = productId.toString(),
        product = product.toDTO(),
        name = name,
        description = description,
        attributes = attributes.map { it.toDTO() }.toMutableList(),
        variantIds = variantIds.map { it.toString() }.toMutableList(),
        variants = variants.toDTO(),
        languageCode = languageCode,
        creationDate = creationDate,
        modifiedDate = modifiedDate,
        createdBy = createdBy.toString()
    )
}
