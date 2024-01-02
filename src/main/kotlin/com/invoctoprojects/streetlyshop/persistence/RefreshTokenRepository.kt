package com.invoctoprojects.streetlyshop.persistence

import com.invoctoprojects.streetlyshop.persistence.domain.customer.RefreshToken
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository : MongoRepository<RefreshToken, ObjectId>
