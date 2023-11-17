package com.invoctoprojects.streetlyshop.persistence

import com.invoctoprojects.streetlyshop.persistence.domain.customer.Customer
import org.bson.types.ObjectId

interface CustomerRepository {
    fun save(customer: Customer): Customer
    fun findById(id: ObjectId): Customer?
    fun getById(id: ObjectId): Customer
    fun findByEmail(email: String): Customer?
    fun getByEmail(email: String): Customer
    fun addFavoriteProduct(customerId: ObjectId, productId: ObjectId)
    fun removeFavoriteProduct(customerId: ObjectId, productId: ObjectId)
}