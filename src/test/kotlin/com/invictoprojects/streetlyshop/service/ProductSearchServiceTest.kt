package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.PaginatedProductSearch
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductSortingOrder
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo
import com.invictoprojects.streetlyshop.util.any
import com.invictoprojects.streetlyshop.web.controller.request.ProductSearchRequest
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
import java.math.BigDecimal
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class ProductSearchServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @InjectMocks
    lateinit var productSearchService: ProductSearchService

    @Test
    fun search_requestIsValid_searchIsPerformed() {
        val attributeValueId = ObjectId()
        val request = ProductSearchRequest(
            query = "White T-Shirt",
            createdBy = ObjectId().toString(),
            categoryId = ObjectId().toString(),
            productStatuses = listOf(ProductStatus.ACTIVE),
            creationDateGT = Instant.now(),
            creationDateLT = Instant.now(),
            modifiedDateGT = Instant.now(),
            modifiedDateLT = Instant.now(),
            currency = Currency.UAH,
            salePriceGT = BigDecimal.ONE,
            salePriceLT = BigDecimal.TEN,
            originalPriceGT = BigDecimal.ONE,
            originalPriceLT = BigDecimal.TEN,
            stockQuantityGT = 0,
            stockQuantityLT = 45,
            productSortingOrder = ProductSortingOrder.HIGHEST_PRICE,
            attributeValueFilter = listOf(listOf(attributeValueId.toString())),
            language = Language.Ua,
            pageSize = 10,
            page = 2
        )

        val variantInfo = VariantInfo(
            contentId = ObjectId(),
            productId = ObjectId(),
            name = "White T-Shirt",
            description = "Cool T-Shirt",
            createdBy = ObjectId(),
            languageCode = Language.Ua,
            product = Product(id = ObjectId(), categoryId = ObjectId(), createdBy = ObjectId()),
            variants = Variant(
                id = ObjectId(),
                barcode = "123",
                productId = ObjectId(),
                contentId = ObjectId(),
                createdBy = ObjectId()
            )
        )

        val productSearchResult = PaginatedProductSearch(
            paginatedResults = mutableListOf(variantInfo),
            totalCount = 1
        )

        given(productRepository.search(any())).willReturn(productSearchResult)

        val actualResult = productSearchService.search(request)

        verify(productRepository).search(request.toAggregation())

        assertThat(actualResult).isEqualTo(productSearchResult.toDTO())
    }

}
