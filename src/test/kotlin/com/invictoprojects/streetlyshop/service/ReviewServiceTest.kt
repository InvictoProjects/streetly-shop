package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.*
import com.invictoprojects.streetlyshop.persistence.domain.model.*
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Review
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.request.AddReviewRequest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.*


@ExtendWith(MockitoExtension::class)
internal class ReviewServiceTest {

    @Mock
    lateinit var productRepository: com.invictoprojects.streetlyshop.persistence.ProductRepository

    @Mock
    lateinit var variantRepository: com.invictoprojects.streetlyshop.persistence.VariantRepository

    @Mock
    lateinit var mediaRepository: com.invictoprojects.streetlyshop.persistence.MediaRepository

    @Mock
    lateinit var customerRepository: com.invictoprojects.streetlyshop.persistence.CustomerRepository

    @Mock
    lateinit var reviewRepository: com.invictoprojects.streetlyshop.persistence.ReviewRepository

    @Mock
    lateinit var authenticationFacade: AuthenticationFacade

    @InjectMocks
    lateinit var reviewService: ReviewService

    @Test
    fun addReview_requestIsValid_reviewIsAdded() {
        // given
        val variantId = ObjectId()
        val contentId = ObjectId()
        val productId = ObjectId()

        val mediaId = ObjectId()
        val request = AddReviewRequest(
            variantId = variantId.toString(),
            mediaIds = listOf(mediaId.toString()),
            text = "Nice T-Shirt",
            rating = 4
        )

        val variant = Variant(
            id = variantId,
            barcode = "123",
            productId = productId,
            contentId = contentId,
            createdBy = ObjectId()
        )
        given(variantRepository.getById(variantId)).willReturn(variant)

        val currentUserId = ObjectId()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        val user = Customer(
            id = currentUserId,
            name = "Vlad",
            nickname = "VladNick",
            email = "vlad@gmail.com",
            password = "encoded"
        )
        given(customerRepository.getById(currentUserId)).willReturn(user)

        val media = Media(id = mediaId, url = "media.url", uploadedBy = ObjectId())
        given(mediaRepository.getById(mediaId)).willReturn(media)

        given(reviewRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Review>())

        val product = Product(
            id = productId,
            categoryId = ObjectId(),
            createdBy = ObjectId(),
            contentIds = mutableListOf(contentId),
            rating = 5.0,
            reviewCount = 1,
            reviewIds = mutableListOf(ObjectId())
        )
        given(productRepository.getById(productId)).willReturn(product)

        // when
        val reviewDTO = reviewService.addReview(request)

        // then
        verify(productRepository).save(product)

        assertThat(reviewDTO.variantId).isEqualTo(variantId.toString())
        assertThat(reviewDTO.createdBy).isEqualTo(currentUserId.toString())
        assertThat(reviewDTO.customerName).isEqualTo("Vlad")
        assertThat(reviewDTO.medias).hasSize(1)
        assertThat(reviewDTO.medias[0]).isEqualTo(media.toDTO())
        assertThat(reviewDTO.rating).isEqualTo(4)
        assertThat(reviewDTO.text).isEqualTo("Nice T-Shirt")

        assertThat(product.rating).isEqualTo(4.5)
        assertThat(product.reviewCount).isEqualTo(2)
        assertThat(product.reviewIds).hasSize(2)
    }

    private fun <T> any(): T = Mockito.any()
}
