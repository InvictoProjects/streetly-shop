package com.invictoprojects.streetlyshop.service

import com.mongodb.assertions.Assertions.assertTrue
import com.invictoprojects.streetlyshop.persistence.ContentRepository
import com.invictoprojects.streetlyshop.persistence.VariantRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.ExchangeRate
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateVariantRequest
import com.invictoprojects.streetlyshop.web.exception.InvalidAttributeException
import com.invictoprojects.streetlyshop.web.exception.UserNotAuthorizedException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.bson.types.Decimal128
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.AdditionalAnswers
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.math.BigDecimal


@ExtendWith(MockitoExtension::class)
internal class VariantServiceTest {

    @Mock
    lateinit var variantRepository: VariantRepository

    @Mock
    lateinit var contentRepository: ContentRepository

    @Mock
    lateinit var attributeService: AttributeService

    @Mock
    lateinit var exchangeRateService: ExchangeRateService

    @Mock
    lateinit var authenticationFacade: AuthenticationFacade

    @InjectMocks
    lateinit var variantService: VariantService

    @Test
    fun createVariant_attributeIsNotValid_exceptionIsThrown() {
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val request = CreateVariantRequest(
            contentId = "6460ca81e6192a239603be01",
            barcode = "1231245",
            attributes = mutableListOf(attribute),
            salePriceUAH = BigDecimal.ONE,
            originalPriceUAH = BigDecimal.TEN,
            stockQuantity = 10
        )

        given(attributeService.validateAttributes(mutableListOf(attribute)))
            .willThrow(InvalidAttributeException("Error"))

        val throwable = catchThrowable { variantService.createVariant(request) }

        assertThat(throwable).isInstanceOf(InvalidAttributeException::class.java)

        verifyNoInteractions(variantRepository, contentRepository)
    }

    @Test
    fun createVariant_userIsNotAuthorized_exceptionIsThrown() {
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val contentId = ObjectId()
        val request = CreateVariantRequest(
            contentId = contentId.toString(),
            barcode = "1231245",
            attributes = mutableListOf(attribute),
            salePriceUAH = BigDecimal.ONE,
            originalPriceUAH = BigDecimal.TEN,
            stockQuantity = 10
        )

        val currentUserId = ObjectId().toString()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId, null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        given(contentRepository.getCreator(contentId, Language.En)).willReturn(ObjectId())

        val throwable = catchThrowable { variantService.createVariant(request) }

        assertThat(throwable).isInstanceOf(UserNotAuthorizedException::class.java)

        verifyNoInteractions(variantRepository)
    }

    @Test
    fun createVariant_requestIsValid_variantIsCreated() {
        // given
        val attribute = AttributeDTO(id = ObjectId().toString(), valueId = ObjectId().toString())
        val contentId = ObjectId().toString()
        val productId = ObjectId()
        val request = CreateVariantRequest(
            contentId = contentId,
            barcode = "1231245",
            attributes = mutableListOf(attribute),
            salePriceUAH = BigDecimal.ONE,
            originalPriceUAH = BigDecimal.TEN,
            stockQuantity = 10
        )

        val currentUserId = ObjectId()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        given(contentRepository.getCreator(contentId.toObjectId(), Language.En)).willReturn(currentUserId)

        val englishContent = Content(
            id = contentId.toObjectId(),
            productId = productId,
            languageCode = Language.En,
            createdBy = currentUserId
        )
        val ukrainianContent = Content(
            id = contentId.toObjectId(),
            productId = productId,
            languageCode = Language.Ua,
            createdBy = currentUserId
        )
        val polishContent = Content(
            id = contentId.toObjectId(),
            productId = productId,
            languageCode = Language.Pl,
            createdBy = currentUserId
        )

        given(contentRepository.getById(contentId.toObjectId(), Language.En)).willReturn(englishContent)
        given(contentRepository.getById(contentId.toObjectId(), Language.Ua)).willReturn(ukrainianContent)
        given(contentRepository.getById(contentId.toObjectId(), Language.Pl)).willReturn(polishContent)

        given(contentRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Content>())
        given(variantRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Variant>())

        given(exchangeRateService.getByCurrency(any())).willReturn(ExchangeRate(id = "UAH-UAH", rate = Decimal128(1)))

        // when
        val variantDTO = variantService.createVariant(request)

        // then
        verify(contentRepository, times(Language.values().size)).save(any())

        assertThat(variantDTO.barcode).isEqualTo(request.barcode)
        assertThat(variantDTO.contentId).isEqualTo(request.contentId)
        assertThat(variantDTO.productId).isEqualTo(productId.toString())
        assertThat(variantDTO.attributes).isEqualTo(request.attributes)
        assertThat(variantDTO.medias).isEqualTo(request.medias)
        assertTrue(
            variantDTO.prices.all { (_, price) ->
                price.originalPrice == BigDecimal.TEN && price.salePrice == BigDecimal.ONE
            }
        )
        assertThat(variantDTO.stock.quantity).isEqualTo(10)
    }

    @Test
    fun updateStock_userIsUnauthorized_exceptionIsThrown() {
        val variantId = ObjectId()
        val stockDelta = 10L

        val authentication = UsernamePasswordAuthenticationToken(ObjectId().toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)
        given(variantRepository.getCreator(variantId)).willReturn(ObjectId())

        val throwable = catchThrowable { variantService.updateStock(variantId.toString(), stockDelta) }

        assertThat(throwable).isInstanceOf(UserNotAuthorizedException::class.java)
        verify(variantRepository, never()).updateStock(variantId, stockDelta)
    }

    @Test
    fun updateStock_userIsValid_stockIsUpdated() {
        val variantId = ObjectId()
        val stockDelta = 10L

        val currentUserId = ObjectId()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)
        given(variantRepository.getCreator(variantId)).willReturn(currentUserId)

        variantService.updateStock(variantId.toString(), stockDelta)

        verify(variantRepository).updateStock(variantId, stockDelta)
    }

    private fun <T> any(): T = Mockito.any()
}
