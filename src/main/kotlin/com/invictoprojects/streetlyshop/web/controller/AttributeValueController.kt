package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.AttributeValueService
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeValueNameRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Attribute Value Controller")
@Validated
@RestController
@RequestMapping("/v1/api/attribute/value")
class AttributeValueController(
        val attributeValueService: AttributeValueService
) {

    @ApiOperation("Update attribute value name")
    @PutMapping("{id}/name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateAttributeValue(
            @PathVariable id: String,
            @Valid @RequestBody request: UpdateAttributeValueNameRequest
    ) {
        attributeValueService.updateName(ObjectId(id), request)
    }
}
