package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDTO
import com.invictoprojects.streetlyshop.web.exception.InvalidAttributeException
import org.springframework.stereotype.Service

@Service
class AttributeService(val attributeValueService: AttributeValueService) {

    fun validateAttributes(attributes: List<AttributeDTO>) {
        attributes.forEach {
            val attributeValue = attributeValueService.getById(it.valueId.toObjectId())
            if (attributeValue.attributeId.toString() != it.id) {
                throw InvalidAttributeException("Attribute id ${it.id} is not valid for value ${it.valueId}")
            }
        }
    }
}
