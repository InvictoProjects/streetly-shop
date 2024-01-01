package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.ProductService
import com.invictoprojects.streetlyshop.web.controller.dto.ProductDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateProductRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateProductRequest
import com.invictoprojects.streetlyshop.web.validator.ValidUpdateProductRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Product Controller")
@Validated
@RestController
@RequestMapping("/v1/api/product")
class ProductController(
    val productService: ProductService
) {

    @ApiOperation("Create product")
    @PostMapping
    fun createProduct(@Valid @RequestBody request: CreateProductRequest): ProductDTO =
        productService.createProduct(request)

    @ApiOperation("Update product")
    @PutMapping("{id}")
    fun updateProduct(
        @PathVariable id: String,
        @ValidUpdateProductRequest @RequestBody request: UpdateProductRequest
    ): ProductDTO =
        productService.updateProduct(id, request)

    @ApiOperation("Get product by id")
    @GetMapping("{id}/{language}")
    fun getProduct(@PathVariable id: String, @PathVariable language: Language): ProductDTO =
        productService.getProduct(id, language)
}
