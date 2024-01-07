package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.AttributeValueRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeValueNameRequest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension


@ExtendWith(MockitoExtension::class)
internal class AttributeValueServiceTest {

    @Mock
    lateinit var attributeValueRepository: AttributeValueRepository

    @InjectMocks
    lateinit var attributeValueService: AttributeValueService

    companion object {
        private const val ATTRIBUTE_VALUE_RED = "RED"
        private const val ATTRIBUTE_VALUE_LRED = "Light Red"
    }

    @Test
    fun updateName_attributeValueIsPresent_nameIsUpdated() {
        val request = UpdateAttributeValueNameRequest(ATTRIBUTE_VALUE_RED, Language.En)
        val attributeValueId = ObjectId()
        val attributeId = ObjectId()

        val attributeValue = AttributeValue(
            id = attributeValueId,
            attributeId = attributeId,
            name = ATTRIBUTE_VALUE_LRED,
            languageCode = Language.En
        )
        given(attributeValueRepository.getById(attributeValueId, Language.En)).willReturn(attributeValue)

        attributeValueService.updateName(attributeValueId, request)

        verify(attributeValueRepository).save(AttributeValue(attributeValueId, attributeId, ATTRIBUTE_VALUE_RED, Language.En))
    }
}
