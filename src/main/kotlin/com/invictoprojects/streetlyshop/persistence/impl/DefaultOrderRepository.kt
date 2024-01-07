package com.invictoprojects.streetlyshop.persistence.impl

import com.invictoprojects.streetlyshop.persistence.OrderRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.order.Order
import com.invictoprojects.streetlyshop.web.exception.OrderNotFoundException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class DefaultOrderRepository(
    @Value("\${mongodb.collection.orders}")
    val ordersCollection: String,
    val mongoTemplate: MongoTemplate
) : OrderRepository {

    override fun save(order: Order): Order {
        return mongoTemplate.save(order, ordersCollection)
    }

    override fun findById(id: ObjectId): Order? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findOne(query, Order::class.java, ordersCollection)
    }

    override fun getById(id: ObjectId): Order {
        return findById(id) ?: throw OrderNotFoundException("Order with id $id was not found")
    }

    override fun existsById(id: ObjectId): Boolean {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists(query, ordersCollection)
    }
}
