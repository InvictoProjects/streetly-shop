package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.CategoryRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Category
import com.invictoprojects.streetlyshop.util.any
import com.invictoprojects.streetlyshop.util.capture
import com.invictoprojects.streetlyshop.web.controller.request.CreateCategoryRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCategoryNameRequest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.AdditionalAnswers
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
internal class CategoryServiceTest {

    @Mock
    lateinit var categoryRepository: CategoryRepository

    @InjectMocks
    lateinit var categoryService: CategoryService

    companion object {
        private const val CATEGORY_NAME = "Dresses"
        private const val CATEGORY_NAME_EN = "Clothes"
        private const val CATEGORY_NAME_UA = "Одяг"
        private const val CATEGORY_NAME_PL = "Odzież"
    }

    @Test
    fun createCategory_categoryIsParent_categoryIsCreated() {
        val request = CreateCategoryRequest(parentCategoryId = null, name = CATEGORY_NAME)

        given(categoryRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Category>())

        val englishCategory = categoryService.createCategory(request)

        verify(categoryRepository, times(Language.values().size)).save(any())

        assertThat(englishCategory.name).isEqualTo(request.name)
    }

    @Test
    fun createCategory_categoryHasParent_categoryIsCreated() {
        val parentCategoryId = ObjectId()
        val request = CreateCategoryRequest(parentCategoryId = parentCategoryId.toString(), name = CATEGORY_NAME)

        val englishParentCategory = Category(id = parentCategoryId, name = CATEGORY_NAME_EN, languageCode = Language.En)
        val ukrainianParentCategory = Category(
            id = parentCategoryId,
            name = CATEGORY_NAME_UA,
            languageCode = Language.Ua
        )
        val polishParentCategory = Category(id = parentCategoryId, name = CATEGORY_NAME_PL, languageCode = Language.Pl)

        given(categoryRepository.getById(parentCategoryId, Language.En)).willReturn(englishParentCategory)
        given(categoryRepository.getById(parentCategoryId, Language.Ua)).willReturn(ukrainianParentCategory)
        given(categoryRepository.getById(parentCategoryId, Language.Pl)).willReturn(polishParentCategory)

        given(categoryRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Category>())

        val englishCategory = categoryService.createCategory(request)

        verify(categoryRepository, times(Language.values().size * 2)).save(any())

        assertThat(englishCategory.name).isEqualTo(request.name)
    }

    @Test
    fun updateName_requestIsValid_nameIsUpdated() {
        val request = UpdateCategoryNameRequest(CATEGORY_NAME_UA, Language.Ua)
        val categoryId = ObjectId()

        val category = Category(
            id = categoryId,
            name = CATEGORY_NAME,
            languageCode = Language.Ua
        )
        given(categoryRepository.getById(categoryId, Language.Ua)).willReturn(category)

        categoryService.updateName(categoryId.toString(), request)

        val categoryCaptor: ArgumentCaptor<Category> = ArgumentCaptor.forClass(Category::class.java)
        verify(categoryRepository).save(capture(categoryCaptor))

        val actualCategory = categoryCaptor.value
        assertThat(actualCategory.name).isEqualTo(request.name)
    }

    @Test
    fun getCategory_categoryExists_categoryDTOIsReturned() {
        val categoryId = ObjectId()
        val subcategoryId = ObjectId()
        val language = Language.En

        val category = Category(
            id = categoryId,
            name = CATEGORY_NAME,
            parentCategoryId = null,
            languageCode = Language.En,
            subcategoryIds = mutableListOf(subcategoryId)
        )

        given(categoryRepository.getById(categoryId, language)).willReturn(category)

        val categoryDTO = categoryService.getCategory(categoryId.toString(), language)

        assertThat(categoryDTO.id).isEqualTo(categoryId.toString())
        assertThat(categoryDTO.name).isEqualTo(category.name)
        assertThat(categoryDTO.subcategoryIds).isEqualTo(category.subcategoryIds.map { it.toString() })
    }

}
