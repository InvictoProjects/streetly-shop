package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ContentRepository
import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.dto.ContentDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateContentRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateContentRequest
import com.invictoprojects.streetlyshop.web.exception.UserNotAuthorizedException
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class ContentService(
        private val productRepository: ProductRepository,
        private val contentRepository: ContentRepository,
        private val attributeService: AttributeService,
        private val authenticationFacade: AuthenticationFacade
) {

    fun createContent(request: CreateContentRequest): ContentDTO {
        attributeService.validateAttributes(request.attributes)

        val productId = request.productId!!.toObjectId()
        val product = productRepository.getById(productId)
        validateUser(product.createdBy)

        val contentId = ObjectId()
        addContentToProduct(product, contentId)

        val englishContent = createContentForEachLanguage(contentId, productId, request)
        return englishContent.toDTO()
    }

    private fun addContentToProduct(product: Product, contentId: ObjectId) {
        product.addContent(contentId)
        productRepository.save(product)
    }

    private fun createContentForEachLanguage(
            contentId: ObjectId,
            productId: ObjectId,
            request: CreateContentRequest
    ): Content {
        val currentUserId = authenticationFacade.getAuthentication().name.toObjectId()
        val contentsForEachLanguage = Language.values()
                .map { language ->
                    Content(
                            id = contentId,
                            productId = productId,
                            name = request.name!!,
                            description = request.description!!,
                            attributes = request.attributes.map { it.toAttribute() }.toMutableList(),
                            languageCode = language,
                            createdBy = currentUserId
                    )
                }
                .map { contentRepository.save(it) }

        return contentsForEachLanguage.first { it.languageCode == Language.En }
    }

    fun updateContent(contentId: String, request: UpdateContentRequest): ContentDTO {
        val content = contentRepository.getById(contentId.toObjectId(), request.language!!)
        validateUser(content.createdBy)

        with(request) {
            name?.let { content.name = it }
            description?.let { content.description = it }
            attributes?.let {
                attributeService.validateAttributes(attributes)
                content.attributes = attributes.map { it.toAttribute() }.toMutableList()
            }
        }

        return contentRepository.save(content).toDTO()
    }

    private fun validateUser(createdBy: ObjectId) {
        val currentUserId = authenticationFacade.getAuthentication().name.toObjectId()
        if (createdBy != currentUserId) {
            throw UserNotAuthorizedException("User with id $currentUserId is not authorized")
        }
    }

    fun getContent(contentId: String, language: Language): ContentDTO {
        return contentRepository.getByIdAggregated(contentId.toObjectId(), language).toDTO()
    }
}
