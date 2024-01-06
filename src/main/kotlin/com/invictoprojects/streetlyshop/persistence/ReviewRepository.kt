package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.product.Review
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ReviewRepository : MongoRepository<Review, ObjectId>
