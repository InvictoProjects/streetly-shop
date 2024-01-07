package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.order.Order
import com.invictoprojects.streetlyshop.persistence.domain.model.order.OrderStatus
import java.time.Instant

data class OrderDTO(
    val id: String,
    val customerId: String,
    val customer: CustomerDTO,
    val creationDate: Instant,
    val modifiedDate: Instant,
    val deliveryService: String,
    val city: String,
    val department: String,
    val recipientName: String,
    val recipientSurname: String,
    val recipientMiddleName: String,
    val status: OrderStatus,
    val lines: List<OrderLineDTO>
)

fun Order.toDTO(): OrderDTO {
    return OrderDTO(
        id = id.toString(),
        customerId = customerId.toString(),
        customer = customer.toDTO(),
        creationDate = creationDate,
        modifiedDate = modifiedDate,
        deliveryService = deliveryService,
        city = city,
        department = department,
        recipientName = recipientName,
        recipientSurname = recipientSurname,
        recipientMiddleName = recipientMiddleName,
        status = status,
        lines = lines.map { it.toDTO() }
    )
}
