package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.VariantService
import com.invictoprojects.streetlyshop.web.controller.dto.VariantDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateVariantRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateStockRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Api("Variant Controller")
@Validated
@RestController
@RequestMapping("/v1/api/variant")
class VariantController(
    val variantService: VariantService
) {

    @ApiOperation("Create variant")
    @PostMapping
    fun createVariant(@Valid @RequestBody request: CreateVariantRequest): VariantDTO =
        variantService.createVariant(request)

    @ApiOperation("Get variant by id")
    @GetMapping("{id}/{language}")
    fun getVariant(@PathVariable id: String, @PathVariable language: Language): VariantDTO =
        variantService.getVariant(id, language)

    @ApiOperation("Update variant stock")
    @PutMapping("{id}/stock")
    fun updateVariantStock(@PathVariable id: String, @Valid @RequestBody request: UpdateStockRequest): Unit =
        variantService.updateStock(id, request.stockDelta!!)
}
