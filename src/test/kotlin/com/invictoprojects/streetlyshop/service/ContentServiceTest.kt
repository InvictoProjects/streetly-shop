package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ContentRepository
import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.Attribute
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeDefinition
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateContentRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateContentRequest
import com.invictoprojects.streetlyshop.web.exception.InvalidAttributeException
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
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

@ExtendWith(MockitoExtension::class)
internal class ContentServiceTest {

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var contentRepository: ContentRepository

    @Mock
    lateinit var attributeService: AttributeService

    @Mock
    lateinit var authenticationFacade: AuthenticationFacade

    @InjectMocks
    lateinit var contentService: ContentService

    @Captor
    lateinit var contentCaptor: ArgumentCaptor<Content>

    @Test
    fun createContent_attributeIsNotValid_exceptionIsThrown() {
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val productId = ObjectId()
        val request = CreateContentRequest(
            productId = productId.toString(),
            name = "T-Shirt | White",
            description = "White Cool T-Shirt",
            attributes = mutableListOf(attribute)
        )

        given(attributeService.validateAttributes(mutableListOf(attribute)))
            .willThrow(InvalidAttributeException("Error"))

        val throwable = catchThrowable { contentService.createContent(request) }

        assertThat(throwable).isInstanceOf(InvalidAttributeException::class.java)

        verifyNoInteractions(productRepository, contentRepository)
    }

    @Test
    fun createContent_userIsNotAuthorized_exceptionIsThrown() {
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val productId = ObjectId()
        val request = CreateContentRequest(
            productId = productId.toString(),
            name = "T-Shirt | White",
            description = "White Cool T-Shirt",
            attributes = mutableListOf(attribute)
        )

        val product = Product(
            id = productId,
            categoryId = ObjectId(),
            createdBy = ObjectId()
        )
        given(productRepository.getById(productId)).willReturn(product)

        val currentUserId = ObjectId().toString()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId, null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        val throwable = catchThrowable { contentService.createContent(request) }

        assertThat(throwable).isInstanceOf(UserNotAuthorizedException::class.java)

        verifyNoInteractions(contentRepository)
    }

    @Test
    fun createContent_requestIsValid_contentsForEachLanguageAreCreated() {
        // given
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val productId = ObjectId()
        val request = CreateContentRequest(
            productId = productId.toString(),
            name = "T-Shirt | White",
            description = "White Cool T-Shirt",
            attributes = mutableListOf(attribute)
        )

        val currentUserId = ObjectId()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        val product = spy(
            Product(
                id = productId,
                categoryId = ObjectId(),
                createdBy = currentUserId
            )
        )
        given(productRepository.getById(productId)).willReturn(product)
        given(contentRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Content>())

        // when
        val contentDTO = contentService.createContent(request)

        // then
        verify(product).addContent(any())
        verify(productRepository).save(product)

        verify(contentRepository, times(Language.values().size)).save(any())

        assertThat(contentDTO.name).isEqualTo(request.name)
        assertThat(contentDTO.description).isEqualTo(request.description)
        assertThat(contentDTO.productId).isEqualTo(request.productId)
        assertThat(contentDTO.attributes).isEqualTo(request.attributes)
    }

    @Test
    fun getContent_argumentsAreValid_contentDTOIsReturned() {
        val contentId = ObjectId()
        val language = Language.En

        val attributeId = ObjectId()
        val attributeValueId = ObjectId()
        val attributeValue = AttributeValue(
            id = attributeValueId,
            attributeId = attributeId,
            name = "S",
            languageCode = language
        )
        val attributeDefinition = AttributeDefinition(
            id = attributeId,
            name = "Size",
            languageCode = language,
            valueIds = listOf(attributeValueId),
            values = listOf(attributeValue)
        )

        val content = Content(
            id = contentId,
            productId = ObjectId(),
            name = "T-Shirt",
            description = "White T-Shirt",
            attributes = mutableListOf(
                Attribute(
                    id = attributeId,
                    valueId = ObjectId(),
                    definition = attributeDefinition,
                    value = attributeValue
                )
            ),
            createdBy = ObjectId(),
            languageCode = language
        )

        given(contentRepository.getByIdAggregated(contentId, language)).willReturn(content)

        val contentDTO = contentService.getContent(contentId.toString(), language)

        assertThat(contentDTO.id).isEqualTo(contentId.toString())
        assertThat(contentDTO.productId).isEqualTo(content.productId.toString())
        assertThat(contentDTO.name).isEqualTo(content.name)
        assertThat(contentDTO.description).isEqualTo(content.description)
        assertThat(contentDTO.attributes).isEqualTo(content.attributes.map { it.toDTO() })
    }

    @Test
    fun updateContent_userIsNotAuthorized_exceptionIsThrown() {
        val contentId = ObjectId()
        val createdBy = ObjectId()
        val request = UpdateContentRequest(
            name = "Red Dress",
            language = Language.En
        )

        val content = Content(
            id = contentId,
            productId = ObjectId(),
            languageCode = Language.En,
            createdBy = createdBy
        )
        given(contentRepository.getById(contentId, Language.En)).willReturn(content)

        val currentUserId = ObjectId().toString()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId, null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        val throwable = catchThrowable { contentService.updateContent(contentId.toString(), request) }

        assertThat(throwable).isInstanceOf(UserNotAuthorizedException::class.java)

        verify(contentRepository, never()).save(any())
    }

    @Test
    fun updateContent_requestAndUserAreValid_contentIsUpdated() {
        // given
        val contentId = ObjectId()
        val createdBy = ObjectId()
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val request = UpdateContentRequest(
            name = "Red Dress",
            description = "Cool Dress",
            attributes = listOf(attribute),
            language = Language.En
        )

        val content = Content(
            id = contentId,
            productId = ObjectId(),
            languageCode = Language.En,
            createdBy = createdBy
        )
        given(contentRepository.getById(contentId, Language.En)).willReturn(content)

        val authentication = UsernamePasswordAuthenticationToken(createdBy.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        given(contentRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Content>())

        // when
        val contentDTO = contentService.updateContent(contentId.toString(), request)

        // then
        verify(contentRepository).save(capture(contentCaptor))

        val actualContent = contentCaptor.value
        assertThat(actualContent.name).isEqualTo("Red Dress")
        assertThat(actualContent.description).isEqualTo("Cool Dress")
        assertThat(actualContent.attributes).hasSize(1)
        assertThat(actualContent.attributes[0].id.toString()).isEqualTo(attribute.id)
        assertThat(actualContent.attributes[0].valueId.toString()).isEqualTo(attribute.valueId)

        assertThat(contentDTO).isEqualTo(actualContent.toDTO())
    }

    private fun <T> any(): T = Mockito.any()
    private fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
}
