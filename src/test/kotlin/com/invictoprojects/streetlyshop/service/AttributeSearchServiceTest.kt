package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeDefinition
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeSearch
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.util.any
import com.invictoprojects.streetlyshop.web.controller.dto.toDTO
import com.invictoprojects.streetlyshop.web.controller.request.AttributeSearchRequest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
internal class AttributeSearchServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @InjectMocks
    lateinit var attributeSearchService: AttributeSearchService

    @Test
    fun search_aggregationIsValid_attributeSearchDTOsAreReturned() {
        val createdBy = ObjectId()
        val categoryId = ObjectId()
        val request = AttributeSearchRequest(
            createdBy = createdBy.toString(),
            categoryId = categoryId.toString(),
            productStatuses = listOf(ProductStatus.ACTIVE),
            language = Language.Ua
        )

        val valueId = ObjectId()
        val definition = AttributeDefinition(
            id = ObjectId(),
            name = "Size",
            starred = true,
            searchable = true,
            priority = 1000,
            values = null,
            languageCode = Language.Ua,
            valueIds = listOf(valueId)
        )
        val attributeSearch = AttributeSearch(
            id = definition.id,
            definition = definition,
            valueIds = listOf(valueId),
            values = listOf(
                AttributeValue(
                    id = valueId,
                    attributeId = definition.id,
                    name = "M",
                    languageCode = Language.Ua
                )
            )
        )

        given(productRepository.searchAttributes(any())).willReturn(listOf(attributeSearch))

        val attributeSearchDTOList = attributeSearchService.search(request)

        verify(productRepository).searchAttributes(request.toAggregation())
        assertThat(attributeSearchDTOList).isEqualTo(listOf(attributeSearch).map { it.toDTO() })
    }

}
