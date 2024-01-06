package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeSearch
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.model.AttributeSearchAggregation
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeSearchDTO
import com.invictoprojects.streetlyshop.web.controller.request.AttributeSearchRequest
import org.springframework.stereotype.Service

@Service
class AttributeSearchService(
    private val productRepository: ProductRepository
) {
    fun search(request: AttributeSearchRequest): List<AttributeSearchDTO> {
        val searchAggregation = request.toAggregation()
        return productRepository.searchAttributes(searchAggregation).map { it.toDTO() }
    }
}

fun AttributeSearchRequest.toAggregation(): AttributeSearchAggregation {
    return AttributeSearchAggregation(
        createdBy = createdBy?.toObjectId(),
        categoryId = categoryId?.toObjectId(),
        productStatuses = productStatuses,
        language = language
    )
}

fun AttributeSearch.toDTO(): AttributeSearchDTO {
    return AttributeSearchDTO(
        id = id.toString(),
        definition = definition.toDTO(),
        values = values.map { it.toDTO() }
    )
}
