package com.invictoprojects.streetlyshop.persistence.domain.model.product

import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.Attribute
import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Product(
    @field:Id
    val id: ObjectId,
    var categoryId: ObjectId,
    val category: Category? = null,
    val createdBy: ObjectId,
    val creationDate: Instant = Instant.now(),
    var modifiedDate: Instant = Instant.now(),
    var contentIds: MutableList<ObjectId> = mutableListOf(),
    var contents: MutableList<Content> = mutableListOf(),
    var attributes: MutableList<Attribute> = mutableListOf(),
    var status: ProductStatus = ProductStatus.DRAFT,
    var rating: Double = 0.0,
    var reviewCount: Int = 0,
    var reviewIds: MutableList<ObjectId> = mutableListOf(),
    var reviews: MutableList<Review> = mutableListOf(),
    var favoriteCount: Long = 0
) {
    fun addContent(contentId: ObjectId): Product {
        contentIds.add(contentId)
        modifiedDate = Instant.now()
        return this
    }

    fun addContents(contents: List<Content>): Product {
        this.contents.addAll(contents)
        return this
    }

    fun updateAttributes(attributes: MutableList<Attribute>) {
        this.attributes = attributes
        modifiedDate = Instant.now()
    }

    fun updateCategoryId(categoryId: ObjectId) {
        this.categoryId = categoryId
        modifiedDate = Instant.now()
    }

    fun updateStatus(productStatus: ProductStatus) {
        this.status = productStatus
        modifiedDate = Instant.now()
    }
}
