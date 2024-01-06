package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.AttributeValueRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.*
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeValueNameRequest
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*


@ExtendWith(MockitoExtension::class)
internal class AttributeValueServiceTest {

    @Mock
    lateinit var attributeValueRepository: com.invictoprojects.streetlyshop.persistence.AttributeValueRepository

    @InjectMocks
    lateinit var attributeValueService: AttributeValueService

    @Test
    fun updateName_attributeValueIsPresent_nameIsUpdated() {
        val request = UpdateAttributeValueNameRequest("Red", Language.En)
        val attributeValueId = ObjectId()
        val attributeId = ObjectId()

        val attributeValue = AttributeValue(
                id = attributeValueId,
                attributeId = attributeId,
                name = "Light Red",
                languageCode = Language.En
        )
        given(attributeValueRepository.getById(attributeValueId, Language.En)).willReturn(attributeValue)

        attributeValueService.updateName(attributeValueId, request)

        verify(attributeValueRepository).save(AttributeValue(attributeValueId, attributeId, "Red", Language.En))
    }
}
