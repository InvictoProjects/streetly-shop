package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.CustomerRepository
import com.invictoprojects.streetlyshop.persistence.MediaRepository
import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.ReviewRepository
import com.invictoprojects.streetlyshop.persistence.VariantRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Review
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.dto.ReviewDTO
import com.invictoprojects.streetlyshop.web.controller.dto.toDTO
import com.invictoprojects.streetlyshop.web.controller.request.AddReviewRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class ReviewService(
    private val productRepository: ProductRepository,
    private val variantRepository: VariantRepository,
    private val mediaRepository: MediaRepository,
    private val customerRepository: CustomerRepository,
    private val reviewRepository: ReviewRepository,
    private val authenticationFacade: AuthenticationFacade
) {

    fun addReview(request: AddReviewRequest): ReviewDTO {
        val variant = variantRepository.getById(request.variantId!!.toObjectId())
        val userId = authenticationFacade.getAuthentication().name.toObjectId()

        val user = customerRepository.getById(userId)
        val medias = request.mediaIds.map { mediaRepository.getById(it.toObjectId()) }

        val reviewId = ObjectId()
        val review = reviewRepository.save(getReview(reviewId, variant, user, medias, request))

        updateProductWithNewReview(variant.productId, review)
        return review.toDTO()
    }

    private fun getReview(
        reviewId: ObjectId,
        variant: Variant,
        user: Customer,
        medias: List<Media>,
        request: AddReviewRequest
    ) = Review(
        id = reviewId,
        productId = variant.productId,
        contentId = variant.contentId,
        variantId = variant.id,
        createdBy = user.id!!,
        customerName = user.name,
        customerAvatar = user.avatar,
        medias = medias.toMutableList(),
        text = request.text!!,
        rating = request.rating!!
    )

    private fun updateProductWithNewReview(productId: ObjectId, review: Review) {
        val product = productRepository.getById(productId)
        product.rating = (product.rating * product.reviewCount + review.rating) / (product.reviewCount + 1)
        product.reviewCount++
        product.reviewIds.add(review.id)

        productRepository.save(product)
    }
}
