package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
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

fun Product.toDTO(): ProductDTO {
    return ProductDTO(
        id = id.toString(),
        categoryId = categoryId.toString(),
        category = category?.toDTO(),
        creationDate = creationDate,
        modifiedDate = modifiedDate,
        contentIds = contentIds.map { it.toString() },
        contents = contents.map { it.toDTO() },
        attributes = attributes.map { it.toDTO() },
        status = status,
        rating = rating,
        reviewCount = reviewCount,
        reviewIds = reviewIds.map { it.toString() },
        reviews = reviews.map { it.toDTO() },
        favoriteCount = favoriteCount
    )
}
