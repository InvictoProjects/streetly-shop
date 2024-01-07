package com.invictoprojects.streetlyshop.util

import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import com.invictoprojects.streetlyshop.persistence.domain.model.order.Order
import com.invictoprojects.streetlyshop.persistence.domain.model.order.OrderLine
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Category
import com.invictoprojects.streetlyshop.persistence.domain.model.product.PaginatedProductSearch
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Review
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.Attribute
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeDefinition
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeSearch
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Stock
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.ExchangeRate
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.Price
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.toDTO
import com.invictoprojects.streetlyshop.web.controller.dto.*

fun AttributeDefinition.toDTO(): AttributeDefinitionDTO {
    return AttributeDefinitionDTO(id.toString(), name, starred, searchable, priority, values?.map { it.toDTO() })
}

fun AttributeValue.toDTO(): AttributeValueDTO {
    return AttributeValueDTO(id.toString(), name)
}

fun AttributeSearch.toDTO(): AttributeSearchDTO {
    return AttributeSearchDTO(
        id = id.toString(),
        definition = definition.toDTO(),
        values = values.map { it.toDTO() }
    )
}

fun Attribute.toDTO(): AttributeDTO {
    return AttributeDTO(
        id = id.toString(),
        valueId = valueId.toString(),
        definition = definition?.toDTO(),
        value = value?.toDTO()
    )
}

fun Category.toDTO(): CategoryDTO {
    return CategoryDTO(
        id = id.toString(),
        parentCategoryId = parentCategoryId?.toString(),
        name = name,
        subcategoryIds = subcategoryIds.map { it.toString() }.toMutableList(),
        creationDate = creationDate,
        modifiedDate = modifiedDate
    )
}

fun Content.toDTO(): ContentDTO {
    return ContentDTO(
        id = id.toString(),
        productId = productId.toString(),
        name = name,
        description = description,
        variantIds = variantIds.map { it.toString() },
        variants = variants.map { it.toDTO() },
        attributes = attributes.map { it.toDTO() },
        creationDate = creationDate,
        modifiedDate = modifiedDate
    )
}

fun Customer.toDTO(): CustomerDTO {
    return CustomerDTO(
        id = id.toString(),
        name = name,
        surname = surname,
        middleName = middleName,
        avatar = avatar,
        phone = phone,
        birthDay = birthDay,
        gender = gender, nickname = nickname,
        email = email,
        registeredAt = registeredAt
    )
}

fun ExchangeRate.toDTO(): ExchangeRateDTO {
    return ExchangeRateDTO(
        modifiedDate = modifiedDate,
        rate = rate.bigDecimalValue()
    )
}

fun Media.toDTO(): MediaDTO {
    return MediaDTO(
        id = id.toString(),
        url = url,
        uploadedBy = uploadedBy.toString(),
        uploadDate = uploadDate
    )
}

fun OrderLine.toDTO(): OrderLineDTO {
    return OrderLineDTO(id = id.toString(), variantInfo = variantInfo.toDTO(), quantity = quantity)
}

fun Order.toDTO(): OrderDTO {
    return OrderDTO(
        id = id.toString(),
        customerId = customerId.toString(),
        customer = customer.toDTO(),
        creationDate = creationDate,
        modifiedDate = modifiedDate,
        deliveryService = deliveryService,
        city = city,
        department = department,
        recipientName = recipientName,
        recipientSurname = recipientSurname,
        recipientMiddleName = recipientMiddleName,
        status = status,
        lines = lines.map { it.toDTO() }
    )
}

fun AttributeDTO.toAttribute(): Attribute {
    return Attribute(id = id.toObjectId(), valueId = valueId.toObjectId())
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

fun Review.toDTO(): ReviewDTO {
    return ReviewDTO(
        id = id.toString(),
        productId = productId.toString(),
        contentId = contentId.toString(),
        variantId = variantId.toString(),
        createdBy = createdBy.toString(),
        customerName = customerName,
        customerAvatar = customerAvatar,
        creationDate = creationDate,
        medias = medias.map { it.toDTO() }.toMutableList(),
        text = text,
        rating = rating
    )
}

fun Variant.toDTO(): VariantDTO {
    return VariantDTO(
        id = id.toString(),
        barcode = barcode,
        productId = productId.toString(),
        contentId = contentId.toString(),
        medias = medias,
        attributes = attributes.map { it.toDTO() },
        creationDate = creationDate,
        modifiedDate = modifiedDate,
        prices = prices.mapValues { (_, price) -> price.toDTO() },
        stock = stock.toDTO()
    )
}

fun Price.toDTO(): PriceDTO {
    return PriceDTO(salePrice = salePrice.bigDecimalValue(), originalPrice = originalPrice.bigDecimalValue())
}

fun Stock.toDTO(): StockDTO {
    return StockDTO(quantity)
}



