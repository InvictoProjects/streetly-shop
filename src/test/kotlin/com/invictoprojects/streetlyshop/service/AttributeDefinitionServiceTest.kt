package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.AttributeDefinitionRepository
import com.invictoprojects.streetlyshop.persistence.AttributeValueRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.*
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeDefinition
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDefinitionDTO
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeValueDTO
import com.invictoprojects.streetlyshop.web.controller.request.AddAttributeDefinitionRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeNameRequest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.AdditionalAnswers.returnsFirstArg
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*


@ExtendWith(MockitoExtension::class)
internal class AttributeDefinitionServiceTest {

    @Mock
    lateinit var attributeDefinitionRepository: AttributeDefinitionRepository

    @Mock
    lateinit var attributeValueRepository: AttributeValueRepository

    @InjectMocks
    lateinit var attributeDefinitionService: AttributeDefinitionService

    @Test
    fun addAttributeDefinition_requestIsValid_attributeDefinitionIsAdded() {
        val request = AddAttributeDefinitionRequest(name = "Size", values = mutableListOf("S", "M", "L"))

        given(attributeValueRepository.save(any())).willAnswer(returnsFirstArg<AttributeValue>())
        given(attributeDefinitionRepository.save(any())).willAnswer(returnsFirstArg<AttributeDefinition>())

        val attributeDefinitionDTO = attributeDefinitionService.addAttributeDefinition(request)

        assertThat(attributeDefinitionDTO.name).isEqualTo("Size")
        assertThat(attributeDefinitionDTO.starred).isEqualTo(request.starred)
        assertThat(attributeDefinitionDTO.searchable).isEqualTo(request.searchable)
        assertThat(attributeDefinitionDTO.priority).isEqualTo(request.priority)
        assertThat(attributeDefinitionDTO.values).hasSize(3)

        verify(attributeValueRepository, times(request.values.size * Language.values().size)).save(any())
        verify(attributeDefinitionRepository, times(Language.values().size)).save(any())
    }

    @Test
    fun updateAttributeName_attributeDefinitionIsPresent_attributeNameIsUpdated() {
        val request = UpdateAttributeNameRequest("Color", Language.En)
        val attributeDefinitionId = ObjectId().toString()

        val attributeDefinition = AttributeDefinition(
            id = ObjectId(attributeDefinitionId),
            name = "Farbe",
            languageCode = Language.En,
            valueIds = listOf(ObjectId())
        )
        given(attributeDefinitionRepository.getById(ObjectId(attributeDefinitionId), Language.En)).willReturn(
            attributeDefinition
        )

        attributeDefinitionService.updateAttributeName(attributeDefinitionId, request)

        val attributeDefinitionCaptor: ArgumentCaptor<AttributeDefinition> =
            ArgumentCaptor.forClass(AttributeDefinition::class.java)
        verify(attributeDefinitionRepository).save(capture(attributeDefinitionCaptor))

        val actualAttribute = attributeDefinitionCaptor.value
        assertThat(actualAttribute.name).isEqualTo("Color")
    }

    @Test
    fun getAttributeDefinition_attributeDefinitionIsPresent_attributeDefinitionIsReturned() {
        val attributeDefinitionId = ObjectId().toString()
        val valueId = ObjectId().toString()
        val attributeDefinition = AttributeDefinition(
            id = ObjectId(attributeDefinitionId),
            name = "Color",
            languageCode = Language.En,
            valueIds = listOf(ObjectId(valueId)),
            values = listOf(AttributeValue(ObjectId(valueId), ObjectId(attributeDefinitionId), "Blue", Language.En))
        )
        given(attributeDefinitionRepository.getByIdAggregated(ObjectId(attributeDefinitionId), Language.En))
            .willReturn(attributeDefinition)

        val attributeDefinitionDTO =
            attributeDefinitionService.getAttributeDefinition(attributeDefinitionId, Language.En)

        val expectedAttributeDefinitionDTO = AttributeDefinitionDTO(
            id = attributeDefinitionId,
            name = attributeDefinition.name,
            starred = attributeDefinition.starred,
            searchable = attributeDefinition.searchable,
            priority = attributeDefinition.priority,
            values = listOf(AttributeValueDTO(valueId, "Blue"))
        )
        assertThat(attributeDefinitionDTO).isEqualTo(expectedAttributeDefinitionDTO)
    }

    private fun <T> any(): T = Mockito.any()
    private fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
}
