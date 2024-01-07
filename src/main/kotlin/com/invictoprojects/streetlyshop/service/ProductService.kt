package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.CategoryRepository
import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.util.toAttribute
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.controller.dto.ProductDTO
import com.invictoprojects.streetlyshop.web.controller.dto.toDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateProductRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateProductRequest
import com.invictoprojects.streetlyshop.web.exception.UserNotAuthorizedException
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val authenticationFacade: AuthenticationFacade,
    private val attributeService: AttributeService
) {
    fun createProduct(request: CreateProductRequest): ProductDTO {
        attributeService.validateAttributes(request.attributes)

        val categoryId = request.categoryId!!.toObjectId()
        val category = categoryRepository.getById(categoryId, Language.En)

        val userId = authenticationFacade.getAuthentication().name.toObjectId()
        val productId = ObjectId()
        val product = Product(
            id = productId,
            createdBy = userId,
            categoryId = category.id,
            attributes = request.attributes.map { it.toAttribute() }.toMutableList()
        )

        return productRepository.save(product).toDTO()
    }

    fun getProduct(productId: String, language: Language): ProductDTO {
        return productRepository.getByIdAggregated(ObjectId(productId), language).toDTO()
    }

    fun updateProduct(productId: String, request: UpdateProductRequest): ProductDTO {
        val product = productRepository.getById(productId.toObjectId())
        validateUser(product)
        updateProductProperties(product, request)
        return productRepository.save(product).toDTO()
    }

    fun validateUser(product: Product) {
        val currentUserId = authenticationFacade.getAuthentication().name.toObjectId()
        if (product.createdBy != currentUserId) {
            throw UserNotAuthorizedException("User with id $currentUserId is not authorized to create content")
        }
    }

    private fun updateProductProperties(product: Product, request: UpdateProductRequest) {
        with(request) {
            attributes?.let { updateProductAttributes(product, attributes) }
            categoryId?.let { updateProductCategory(product, categoryId.toObjectId()) }
            productStatus?.let { product.updateStatus(productStatus) }
        }
    }

    private fun updateProductAttributes(product: Product, attributes: List<AttributeDTO>) {
        attributeService.validateAttributes(attributes)
        product.updateAttributes(attributes.map { it.toAttribute() }.toMutableList())
    }

    private fun updateProductCategory(product: Product, categoryId: ObjectId) {
        val category = categoryRepository.getById(categoryId, Language.En)
        product.updateCategoryId(category.id)
    }
}
