package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.AttributeValueRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeValueNameRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class AttributeValueService(val attributeValueRepository: AttributeValueRepository) {

    fun updateName(id: ObjectId, request: UpdateAttributeValueNameRequest) {
        val attributeValue = attributeValueRepository.getById(id, request.language!!)

        attributeValue.name = request.name!!
        attributeValueRepository.save(attributeValue)
    }

    fun getById(id: ObjectId): AttributeValue {
        return attributeValueRepository.getById(id)
    }
}
