package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductSortingOrder
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import com.invictoprojects.streetlyshop.web.validator.ValidAttributeFilter
import java.math.BigDecimal
import java.time.Instant
import javax.validation.constraints.*

data class ProductSearchRequest(
    val query: String? = null,
    val createdBy: String? = null,
    val categoryId: String? = null,
    val productStatuses: List<ProductStatus> = listOf(ProductStatus.ACTIVE),
    val creationDateGT: Instant? = null,
    val creationDateLT: Instant? = null,
    val modifiedDateGT: Instant? = null,
    val modifiedDateLT: Instant? = null,
    val currency: Currency = Currency.UAH,
    val salePriceGT: BigDecimal? = null,
    val salePriceLT: BigDecimal? = null,
    val originalPriceGT: BigDecimal? = null,
    val originalPriceLT: BigDecimal? = null,
    val stockQuantityGT: Long = 0,
    val stockQuantityLT: Long? = null,
    val productSortingOrder: ProductSortingOrder = ProductSortingOrder.LOWEST_PRICE,
    @field:ValidAttributeFilter
    val attributeValueFilter: List<List<String>>? = null,
    val language: Language = Language.Ua,

    @field:NotNull
    @field:Min(1)
    @field:Max(40)
    val pageSize: Long = 10,

    @field:NotNull
    @field:Min(1)
    val page: Long = 1
)
