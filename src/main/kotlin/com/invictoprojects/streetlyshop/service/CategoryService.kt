package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.CategoryRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Category
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.util.toDTO
import com.invictoprojects.streetlyshop.web.controller.dto.CategoryDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateCategoryRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCategoryNameRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) {

    fun createCategory(request: CreateCategoryRequest): CategoryDTO {
        val categoryId = ObjectId()
        val parentCategoryId = request.parentCategoryId?.toObjectId()

        parentCategoryId?.let { addSubcategoryForEachLanguageCategory(parentCategoryId, categoryId) }

        val englishCategory = createCategoryForEachLanguage(categoryId, parentCategoryId, request)
        return englishCategory.toDTO()
    }

    private fun addSubcategoryForEachLanguageCategory(parentCategoryId: ObjectId, subcategoryId: ObjectId) {
        Language.values()
            .map { categoryRepository.getById(parentCategoryId, it) }
            .map { category -> category.addSubcategory(subcategoryId) }
            .map { category -> categoryRepository.save(category) }
    }

    private fun createCategoryForEachLanguage(
        categoryId: ObjectId,
        parentCategoryId: ObjectId?,
        request: CreateCategoryRequest
    ): Category {
        val categoriesForEachLanguage = Language.values()
            .map { language ->
                Category(
                    id = categoryId,
                    parentCategoryId = parentCategoryId,
                    name = request.name!!,
                    languageCode = language
                )
            }
            .map { categoryRepository.save(it) }

        return categoriesForEachLanguage.first { it.languageCode == Language.En }
    }

    fun updateName(id: String, request: UpdateCategoryNameRequest) {
        val category = categoryRepository.getById(id.toObjectId(), request.language!!)

        category.updateName(request.name!!)
        categoryRepository.save(category)
    }

    fun getCategory(categoryId: String, language: Language): CategoryDTO {
        return categoryRepository.getById(categoryId.toObjectId(), language).toDTO()
    }
}

