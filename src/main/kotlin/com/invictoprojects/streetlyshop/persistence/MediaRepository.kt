package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import org.bson.types.ObjectId

interface MediaRepository {
    fun save(media: Media): Media
    fun findById(id: ObjectId): Media?
    fun getById(id: ObjectId): Media
    fun getByUploadedBy(uploadedBy: ObjectId): List<Media>
}
