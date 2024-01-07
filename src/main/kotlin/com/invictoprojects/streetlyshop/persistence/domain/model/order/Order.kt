package com.invictoprojects.streetlyshop.persistence.domain.model.order

import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class Order(
    @field:Id
    val id: ObjectId,
    val customerId: ObjectId,
    val customer: Customer,
    val creationDate: Instant = Instant.now(),
    var modifiedDate: Instant = Instant.now(),
    var deliveryService: String,
    var city: String,
    var department: String,
    val recipientName: String,
    val recipientSurname: String,
    val recipientMiddleName: String,
    val lines: List<OrderLine>,
    var status: OrderStatus
) {
    fun updateStatus(status: OrderStatus) {
        this.status = status
        modifiedDate = Instant.now()
    }
}
