package com.invoctoprojects.streetlyshop.persistence.impl

import com.invoctoprojects.streetlyshop.persistence.CustomerRepository
import com.invoctoprojects.streetlyshop.persistence.domain.customer.Customer
import com.invoctoprojects.streetlyshop.web.exception.UserNotFoundException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DefaultCustomerRepository(
    @Value("\${mongodb.collection.customers}")
    val customersCollection: String,
    val mongoTemplate: MongoTemplate
) : CustomerRepository {

    override fun save(customer: Customer): Customer {
        return mongoTemplate.save(customer, customersCollection)
    }

    override fun findById(id: ObjectId): Customer? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, Customer::class.java, customersCollection)
    }

    override fun getById(id: ObjectId): Customer {
        return findById(id)
            ?: throw UserNotFoundException("User with id $id was not found")
    }

    override fun findByEmail(email: String): Customer? {
        val query = Query.query(Criteria.where("email").isEqualTo(email))
        return mongoTemplate.findOne(query, Customer::class.java, customersCollection)
    }

    override fun getByEmail(email: String): Customer {
        return findByEmail(email) ?: throw UserNotFoundException("User with email $email not found")
    }

    override fun addFavoriteProduct(customerId: ObjectId, productId: ObjectId) {
        val query = Query(Criteria.where("_id").isEqualTo(customerId))
        val update = Update().addToSet("favoriteProductIds", productId)
        mongoTemplate.updateFirst(query, update, customersCollection)
    }

    override fun removeFavoriteProduct(customerId: ObjectId, productId: ObjectId) {
        val query = Query(Criteria.where("_id").isEqualTo(customerId))
        val update = Update().pull("favoriteProductIds", productId)
        mongoTemplate.updateFirst(query, update, customersCollection)
    }
}
