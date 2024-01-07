package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.ContentService
import com.invictoprojects.streetlyshop.web.controller.dto.ContentDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateContentRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateContentRequest
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

@Api("Content Controller")
@Validated
@RestController
@RequestMapping("/v1/api/content")
class ContentController(
        val contentService: ContentService
) {

    @ApiOperation("Create content")
    @PostMapping
    fun createContent(@Valid @RequestBody request: CreateContentRequest): ContentDTO =
            contentService.createContent(request)

    @ApiOperation("Update content")
    @PutMapping("{id}")
    fun updateContent(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateContentRequest
    ): ContentDTO =
            contentService.updateContent(id, request)

    @ApiOperation("Get content by id")
    @GetMapping("{id}/{language}")
    fun getContent(@PathVariable id: String, @PathVariable language: Language): ContentDTO =
            contentService.getContent(id, language)
}

