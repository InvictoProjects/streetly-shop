package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.CategoryRepository
import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Category
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.ProductStatus
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.util.any
import com.invictoprojects.streetlyshop.util.capture
import com.invictoprojects.streetlyshop.util.toAttribute
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.controller.dto.toDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateProductRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateProductRequest
import com.invictoprojects.streetlyshop.web.exception.InvalidAttributeException
import com.invictoprojects.streetlyshop.web.exception.ProductNotFoundException
import com.invictoprojects.streetlyshop.web.exception.UserNotAuthorizedException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.AdditionalAnswers
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@ExtendWith(MockitoExtension::class)
internal class ProductServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var categoryRepository: CategoryRepository

    @Mock
    lateinit var attributeService: AttributeService

    @Mock
    lateinit var authenticationFacade: AuthenticationFacade

    @Captor
    lateinit var productCaptor: ArgumentCaptor<Product>

    @InjectMocks
    lateinit var productService: ProductService

    @Test
    fun createProduct_attributeIsNotValid_exceptionIsThrown() {
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val request = CreateProductRequest(
            categoryId = ObjectId().toString(),
            attributes = listOf(attribute)
        )

        given(attributeService.validateAttributes(listOf(attribute))).willThrow(InvalidAttributeException("Error"))

        val throwable = catchThrowable { productService.createProduct(request) }

        assertThat(throwable).isInstanceOf(InvalidAttributeException::class.java)

        verifyNoInteractions(productRepository, categoryRepository, authenticationFacade)
    }

    @Test
    fun createProduct_requestIsValid_productIsCreated() {
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val categoryId = ObjectId()
        val request = CreateProductRequest(
            categoryId = categoryId.toString(),
            attributes = listOf(attribute)
        )

        val category = Category(id = categoryId, languageCode = Language.En)
        given(categoryRepository.getById(categoryId, Language.En)).willReturn(category)

        val currentUserId = ObjectId()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        given(productRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Product>())

        val productDTO = productService.createProduct(request)

        verify(productRepository).save(capture(productCaptor))

        val actualProduct = productCaptor.value
        assertThat(actualProduct.categoryId).isEqualTo(categoryId)
        assertThat(actualProduct.createdBy).isEqualTo(currentUserId)
        assertThat(actualProduct.attributes).isEqualTo(request.attributes.map { it.toAttribute() })

        assertThat(productDTO).isEqualTo(actualProduct.toDTO())
    }

    @Test
    fun getProduct_productExists_productDTOIsReturned() {
        val productId = ObjectId()
        val product = Product(id = productId, categoryId = ObjectId(), createdBy = ObjectId())

        given(productRepository.getByIdAggregated(productId, Language.En)).willReturn(product)

        val productDTO = productService.getProduct(productId.toString(), Language.En)

        assertThat(productDTO).isEqualTo(product.toDTO())
    }

    @Test
    fun updateProduct_productDoesNotExist_exceptionIsThrown() {
        val productId = ObjectId()
        val request = UpdateProductRequest(categoryId = ObjectId().toString())

        given(productRepository.getById(productId)).willThrow(ProductNotFoundException("error"))

        val throwable = catchThrowable { productService.updateProduct(productId.toString(), request) }

        assertThat(throwable).isInstanceOf(ProductNotFoundException::class.java)
    }

    @Test
    fun updateProduct_userIsNotAuthorized_exceptionIsThrown() {
        val productId = ObjectId()
        val request = UpdateProductRequest(categoryId = ObjectId().toString())

        val product = Product(id = productId, categoryId = ObjectId(), createdBy = ObjectId())
        given(productRepository.getById(productId)).willReturn(product)

        val currentUserId = ObjectId()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        val throwable = catchThrowable { productService.updateProduct(productId.toString(), request) }

        assertThat(throwable).isInstanceOf(UserNotAuthorizedException::class.java)
    }

    @Test
    fun updateProduct_requestIsValid_productIsUpdated() {
        val productId = ObjectId()
        val categoryId = ObjectId()

        val attributeId = ObjectId()
        val attributeValueId = ObjectId()
        val attributes = listOf(AttributeDTO(id = attributeId.toString(), valueId = attributeValueId.toString()))

        val request = UpdateProductRequest(
            categoryId = categoryId.toString(),
            attributes = attributes,
            productStatus = ProductStatus.ACTIVE
        )

        val currentUserId = ObjectId()
        val product = Product(id = productId, categoryId = ObjectId(), createdBy = currentUserId)
        given(productRepository.getById(productId)).willReturn(product)

        val authentication = UsernamePasswordAuthenticationToken(currentUserId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        val category = Category(id = categoryId, languageCode = Language.En)
        given(categoryRepository.getById(categoryId, Language.En)).willReturn(category)

        given(productRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Product>())

        val productDTO = productService.updateProduct(productId.toString(), request)

        verify(productRepository).save(capture(productCaptor))

        val actualProduct = productCaptor.value
        assertThat(actualProduct.categoryId).isEqualTo(categoryId)
        assertThat(actualProduct.attributes).hasSize(1)
        assertThat(actualProduct.attributes[0].id).isEqualTo(attributeId)
        assertThat(actualProduct.attributes[0].valueId).isEqualTo(attributeValueId)
        assertThat(actualProduct.status).isEqualTo(ProductStatus.ACTIVE)

        assertThat(productDTO).isEqualTo(actualProduct.toDTO())
    }

}
