package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import java.time.Instant

data class ProductDTO(
    var id: String,
    var categoryId: String,
    var category: CategoryDTO?,
    var creationDate: Instant,
    val modifiedDate: Instant,
    var contentIds: List<String>,
    var contents: List<ContentDTO>,
    var attributes: List<AttributeDTO>,
    var status: ProductStatus,
    var rating: Double,
    var reviewCount: Int,
    var reviewIds: List<String>,
    var reviews: List<ReviewDTO>,
    var favoriteCount: Long
)
