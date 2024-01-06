package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.Currency
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo
import org.bson.types.ObjectId
import java.math.BigDecimal

interface VariantRepository {
    fun save(variant: Variant): Variant
    fun findByIdAggregated(id: ObjectId, language: Language): Variant?
    fun getByIdAggregated(id: ObjectId, language: Language): Variant
    fun findById(id: ObjectId): Variant?
    fun getById(id: ObjectId): Variant
    fun updatePricesWithNewExchangeRate(currency: Currency, exchangeRate: BigDecimal)
    fun updateStock(id: ObjectId, stockDelta: Long)
    fun findVariantInfoById(id: ObjectId, language: Language): VariantInfo?
    fun getVariantInfoById(id: ObjectId, language: Language): VariantInfo
    fun getCreator(id: ObjectId): ObjectId
}
