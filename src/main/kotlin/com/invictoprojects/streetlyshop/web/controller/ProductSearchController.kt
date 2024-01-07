package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.ProductSearchService
import com.invictoprojects.streetlyshop.web.controller.dto.PaginatedProductSearchDTO
import com.invictoprojects.streetlyshop.web.controller.request.ProductSearchRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Product Search Controller")
@Validated
@RestController
@RequestMapping("/v1/api/product/search")
class ProductSearchController(
    val productSearchService: ProductSearchService
) {

    @ApiOperation("Search products")
    @PostMapping
    fun searchProduct(@Valid @RequestBody request: ProductSearchRequest): PaginatedProductSearchDTO =
        productSearchService.search(request)
}
