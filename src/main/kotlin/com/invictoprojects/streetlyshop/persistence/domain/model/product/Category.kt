package com.invictoprojects.streetlyshop.persistence.domain.model.product

import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Category(
    @field:Id
    val id: ObjectId,
    var name: String = "",
    var parentCategoryId: ObjectId? = null,
    var subcategoryIds: MutableList<ObjectId> = mutableListOf(),
    var languageCode: Language,
    val creationDate: Instant = Instant.now(),
    var modifiedDate: Instant = Instant.now()
) {
    fun updateName(name: String): Category {
        this.name = name
        modifiedDate = Instant.now()
        return this
    }

    fun addSubcategory(subcategoryId: ObjectId): Category {
        subcategoryIds.add(subcategoryId)
        modifiedDate = Instant.now()
        return this
    }
}
