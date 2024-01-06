package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.AttributeDefinitionService
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDefinitionDTO
import com.invictoprojects.streetlyshop.web.controller.request.AddAttributeDefinitionRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeNameRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Attribute Definition Controller")
@Validated
@RestController
@RequestMapping("/v1/api/attribute-definition")
class AttributeDefinitionController(
    val attributeDefinitionService: AttributeDefinitionService
) {

    @ApiOperation("Add attribute definition")
    @PostMapping
    fun addAttributeDefinition(@Valid @RequestBody request: AddAttributeDefinitionRequest): AttributeDefinitionDTO =
        attributeDefinitionService.addAttributeDefinition(request)

    @ApiOperation("Update attribute name")
    @PutMapping("{id}/name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAttributeName(@PathVariable id: String, @Valid @RequestBody request: UpdateAttributeNameRequest) =
        attributeDefinitionService.updateAttributeName(id, request)

    @ApiOperation("Get attribute definition by id")
    @GetMapping("{id}/{language}")
    fun getAttributeDefinition(@PathVariable id: String, @PathVariable language: Language): AttributeDefinitionDTO =
        attributeDefinitionService.getAttributeDefinition(id, language)
}
