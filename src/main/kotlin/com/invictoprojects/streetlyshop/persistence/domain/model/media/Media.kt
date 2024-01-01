package com.invictoprojects.streetlyshop.persistence.domain.model.media

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Media(
    @field:Id
    val id: ObjectId,
    var url: String,
    var uploadedBy: ObjectId,
    val uploadDate: Instant = Instant.now()
)
