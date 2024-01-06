package com.invictoprojects.streetlyshop.persistence.impl

import com.invictoprojects.streetlyshop.persistence.*
import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import com.invictoprojects.streetlyshop.web.exception.MediaNotFoundException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DefaultMediaRepository(
    @Value("\${mongodb.collection.medias}")
    val mediasCollection: String,
    val mongoTemplate: MongoTemplate
) : MediaRepository {
    override fun save(media: Media): Media {
        return mongoTemplate.save(media, mediasCollection)
    }

    override fun findById(id: ObjectId): Media? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, Media::class.java, mediasCollection)
    }

    override fun getById(id: ObjectId): Media {
        return findById(id) ?: throw MediaNotFoundException("Media with id $id was not found")
    }

    override fun getByUploadedBy(uploadedBy: ObjectId): List<Media> {
        val query = Query.query(Criteria.where("uploadedBy").isEqualTo(uploadedBy))
        return mongoTemplate.find(query, Media::class.java, mediasCollection)
    }
}
