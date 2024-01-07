package com.invictoprojects.streetlyshop.persistence.domain.model.product

enum class ProductSortingOrder(val direction: Int, val field: String) {
    LOWEST_PRICE(1, "variants.price.UAH.salePrice"),
    HIGHEST_PRICE(-1, "variants.price.UAH.salePrice"),
    NEW_ARRIVALS(-1, "product.creationDate"),
    MOST_RATED(-1, "product.rating"),
    MOST_FAVORITED(-1, "product.favoriteCount")
}
