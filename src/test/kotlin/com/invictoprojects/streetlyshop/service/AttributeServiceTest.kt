package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.domain.model.*
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.exception.InvalidAttributeException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
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
internal class AttributeServiceTest {

    @Mock
    lateinit var attributeValueService: AttributeValueService

    @InjectMocks
    lateinit var attributeService: AttributeService

    @Test
    fun validateAttributes_attributesAreValid_ok() {
        val attributeId = ObjectId()
        val attributeValueS = AttributeValue(ObjectId(), attributeId, "S", Language.En)
        val attributeValueM = AttributeValue(ObjectId(), attributeId, "M", Language.En)

        given(attributeValueService.getById(attributeValueS.id)).willReturn(attributeValueS)
        given(attributeValueService.getById(attributeValueM.id)).willReturn(attributeValueM)

        val attributeDTOList = listOf(
            AttributeDTO(attributeId.toString(), attributeValueS.id.toString()),
            AttributeDTO(attributeId.toString(), attributeValueM.id.toString()),
        )
        attributeService.validateAttributes(attributeDTOList)
    }

    @Test
    fun validateAttributes_attributesAreNotValid_exceptionIsThrown() {
        val attributeId = ObjectId()
        val attributeValueS = AttributeValue(ObjectId(), attributeId, "S", Language.En)
        val attributeValueM = AttributeValue(ObjectId(), attributeId, "M", Language.En)

        given(attributeValueService.getById(attributeValueS.id)).willReturn(attributeValueS)

        val attributeDTOList = listOf(
            AttributeDTO(ObjectId().toString(), attributeValueS.id.toString()),
            AttributeDTO(ObjectId().toString(), attributeValueM.id.toString()),
        )

        val throwable = catchThrowable { attributeService.validateAttributes(attributeDTOList) }

        assertThat(throwable).isInstanceOf(InvalidAttributeException::class.java)
    }
}
