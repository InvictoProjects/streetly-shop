package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.order.Order
import org.bson.types.ObjectId

interface OrderRepository {
    fun save(order: Order): Order
    fun existsById(id: ObjectId): Boolean
    fun findById(id: ObjectId): Order?
    fun getById(id: ObjectId): Order
}
