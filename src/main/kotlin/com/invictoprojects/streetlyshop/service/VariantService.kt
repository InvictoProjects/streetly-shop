package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.ContentRepository
import com.invictoprojects.streetlyshop.persistence.VariantRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.content.Content
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Stock
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.Price
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.util.toAttribute
import com.invictoprojects.streetlyshop.util.toDTO
import com.invictoprojects.streetlyshop.util.toDecimal128
import com.invictoprojects.streetlyshop.web.controller.dto.VariantDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateVariantRequest
import com.invictoprojects.streetlyshop.web.exception.UserNotAuthorizedException
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class VariantService(
    private val variantRepository: VariantRepository,
    private val contentRepository: ContentRepository,
    private val exchangeRateService: ExchangeRateService,
    private val attributeService: AttributeService,
    private val authenticationFacade: AuthenticationFacade
) {

    fun createVariant(request: CreateVariantRequest): VariantDTO {
        attributeService.validateAttributes(request.attributes)

        val contentId = request.contentId!!.toObjectId()
        val userId = authenticationFacade.getAuthentication().name.toObjectId()
        val createdBy = contentRepository.getCreator(contentId, Language.En)
        validateUser(createdBy, userId)

        val variantId = ObjectId()
        val englishContent = addVariantForEachLanguageContent(variantId, contentId)

        val prices = calculatePricesForEachCurrency(request.salePriceUAH!!, request.originalPriceUAH!!)
        val variant = Variant(
            id = variantId,
            barcode = request.barcode!!,
            productId = englishContent.productId,
            contentId = contentId,
            attributes = request.attributes.map { it.toAttribute() }.toMutableList(),
            medias = request.medias,
            createdBy = userId,
            prices = prices,
            stock = Stock(request.stockQuantity)
        )

        return variantRepository.save(variant).toDTO()
    }

    fun getVariant(variantId: String, language: Language): VariantDTO {
        return variantRepository.getByIdAggregated(variantId.toObjectId(), language).toDTO()
    }

    fun updateStock(variantId: String, stockDelta: Long) {
        val userId = authenticationFacade.getAuthentication().name.toObjectId()
        val createdBy = variantRepository.getCreator(variantId.toObjectId())
        validateUser(createdBy, userId)

        variantRepository.updateStock(variantId.toObjectId(), stockDelta)
    }

    private fun validateUser(createdBy: ObjectId, currentUserId: ObjectId) {
        if (createdBy != currentUserId) {
            throw UserNotAuthorizedException("User with id $currentUserId is not authorized")
        }
    }

    private fun addVariantForEachLanguageContent(variantId: ObjectId, contentId: ObjectId): Content {
        return Language.values()
            .map { contentRepository.getById(contentId, it) }
            .map { content -> content.addVariant(variantId) }
            .map { content -> contentRepository.save(content) }
            .first { it.languageCode == Language.En }
    }

    private fun calculatePricesForEachCurrency(
        salePriceUAH: BigDecimal,
        originalPriceUAH: BigDecimal
    ): MutableMap<Currency, Price> {
        return Currency.values()
            .associateWith { currency -> calculatePriceForCurrency(salePriceUAH, originalPriceUAH, currency) }
            .toMutableMap()
    }

    private fun calculatePriceForCurrency(
        salePriceUAH: BigDecimal,
        originalPriceUAH: BigDecimal, currency: Currency
    ): Price {
        val exchangeRate = exchangeRateService.getByCurrency(currency).rate.bigDecimalValue()
        val salePrice = salePriceUAH.multiply(exchangeRate)
        val originalPrice = originalPriceUAH.multiply(exchangeRate)
        return Price(salePrice = salePrice.toDecimal128(), originalPrice = originalPrice.toDecimal128())
    }
}

