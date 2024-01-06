package com.invictoprojects.streetlyshop.web.controller.dto

import java.time.Instant

data class ReviewDTO(
    val id: String,
    val productId: String,
    val contentId: String,
    val variantId: String,
    val createdBy: String,
    val customerName: String?,
    val customerAvatar: String?,
    val creationDate: Instant,
    var medias: MutableList<MediaDTO>,
    val text: String,
    val rating: Int
)
