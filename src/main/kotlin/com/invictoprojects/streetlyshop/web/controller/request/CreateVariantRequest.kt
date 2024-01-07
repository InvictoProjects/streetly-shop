package com.invictoprojects.streetlyshop.web.controller.request

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantMedia
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class CreateVariantRequest(
    @field:NotBlank
    val contentId: String? = null,
    @field:NotBlank
    val barcode: String? = null,
    @field:Size(min = 1)
    val attributes: MutableList<AttributeDTO> = mutableListOf(),
    val medias: MutableList<VariantMedia> = mutableListOf(),
    @field:NotNull
    @field:DecimalMin("0")
    val salePriceUAH: BigDecimal? = null,
    @field:NotNull
    @field:DecimalMin("0")
    val originalPriceUAH: BigDecimal? = null,
    @field:Min(0)
    val stockQuantity: Long = 0
)
