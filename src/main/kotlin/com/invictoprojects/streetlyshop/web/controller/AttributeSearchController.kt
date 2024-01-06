package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.AttributeSearchService
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeSearchDTO
import com.invictoprojects.streetlyshop.web.controller.request.AttributeSearchRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Api("Attribute Search Controller")
@Validated
@RestController
@RequestMapping("/v1/api/attribute/search")
class AttributeSearchController(
    val attributeSearchService: AttributeSearchService
) {

    @ApiOperation("Search attributes")
    @PostMapping
    fun searchAttributes(@Valid @RequestBody request: AttributeSearchRequest): List<AttributeSearchDTO> =
        attributeSearchService.search(request)
}
