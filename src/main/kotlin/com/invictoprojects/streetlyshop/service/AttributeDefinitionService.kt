package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.AttributeDefinitionRepository
import com.invictoprojects.streetlyshop.persistence.AttributeValueRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeDefinition
import com.invictoprojects.streetlyshop.persistence.domain.model.product.attribute.AttributeValue
import com.invictoprojects.streetlyshop.util.toDTO
import com.invictoprojects.streetlyshop.web.controller.dto.AttributeDefinitionDTO
import com.invictoprojects.streetlyshop.web.controller.request.AddAttributeDefinitionRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateAttributeNameRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class AttributeDefinitionService(
    val attributeDefinitionRepository: AttributeDefinitionRepository,
    val attributeValueRepository: AttributeValueRepository
) {

    fun addAttributeDefinition(request: AddAttributeDefinitionRequest): AttributeDefinitionDTO {
        val attributeDefinitionId = ObjectId()
        val attributeValueIds = generateAttributeValueIds(request.values.size)
        val attributeValuesInEnglish =
            createAttributeValuesForEachLanguage(attributeDefinitionId, attributeValueIds, request.values)

        val attributeDefinitionInEnglish =
            createAttributeDefinitionForEachLanguage(attributeDefinitionId, request, attributeValueIds)
        attributeDefinitionInEnglish.values = attributeValuesInEnglish
        return attributeDefinitionInEnglish.toDTO()
    }

    fun updateAttributeName(attributeDefinitionId: String, request: UpdateAttributeNameRequest) {
        val attributeDefinition =
            attributeDefinitionRepository.getById(ObjectId(attributeDefinitionId), request.language!!)

        attributeDefinition.name = request.name!!
        attributeDefinitionRepository.save(attributeDefinition)
    }

    fun getAttributeDefinition(attributeId: String, language: Language): AttributeDefinitionDTO {
        return attributeDefinitionRepository.getByIdAggregated(ObjectId(attributeId), language).toDTO()
    }

    private fun generateAttributeValueIds(size: Int) = List(size) { ObjectId() }

    private fun createAttributeValuesForEachLanguage(
        attributeDefinitionId: ObjectId,
        attributeValueIds: List<ObjectId>,
        values: List<String>
    ): List<AttributeValue> {
        return attributeValueIds.zip(values)
            .flatMap { (id, value) ->
                Language.values().map { language ->
                    AttributeValue(
                        id = id,
                        attributeId = attributeDefinitionId,
                        name = value,
                        languageCode = language
                    )
                }
            }
            .map { attributeValueRepository.save(it) }
            .filter { it.languageCode == Language.En }
    }

    private fun createAttributeDefinitionForEachLanguage(
        attributeDefinitionId: ObjectId,
        request: AddAttributeDefinitionRequest,
        attributeValueIds: List<ObjectId>
    ): AttributeDefinition {
        val attributeDefinitionsForEachLanguage = Language.values()
            .map {
                AttributeDefinition(
                    id = attributeDefinitionId,
                    name = request.name!!,
                    starred = request.starred,
                    searchable = request.searchable,
                    priority = request.priority,
                    languageCode = it,
                    valueIds = attributeValueIds
                )
            }
            .map { attributeDefinitionRepository.save(it) }

        return attributeDefinitionsForEachLanguage.first { it.languageCode == Language.En }
    }
}
