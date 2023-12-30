package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.service.CategoryService
import com.invictoprojects.streetlyshop.web.controller.dto.CategoryDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateCategoryRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCategoryNameRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Category Controller")
@Validated
@RestController
@RequestMapping("/v1/api/category")
class CategoryController(
    val categoryService: CategoryService
) {

    @ApiOperation("Create category")
    @PostMapping
    fun createCategory(@Valid @RequestBody request: CreateCategoryRequest): CategoryDTO =
        categoryService.createCategory(request)

    @ApiOperation("Update category name")
    @PutMapping("{id}/name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateName(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateCategoryNameRequest
    ) {
        categoryService.updateName(id, request)
    }

    @ApiOperation("Get category by id")
    @GetMapping("{id}/{language}")
    fun getCategory(@PathVariable id: String, @PathVariable language: Language): CategoryDTO =
        categoryService.getCategory(id, language)
}
