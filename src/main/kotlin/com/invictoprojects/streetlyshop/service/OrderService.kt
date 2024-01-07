package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.CustomerRepository
import com.invictoprojects.streetlyshop.persistence.OrderRepository
import com.invictoprojects.streetlyshop.persistence.VariantRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.Language
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.order.Order
import com.invictoprojects.streetlyshop.persistence.domain.model.order.OrderLine
import com.invictoprojects.streetlyshop.persistence.domain.model.order.OrderStatus
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.dto.OrderDTO
import com.invictoprojects.streetlyshop.web.controller.dto.toDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateOrderRequest
import com.invictoprojects.streetlyshop.web.controller.request.OrderLineRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateOrderStatusRequest
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val variantRepository: VariantRepository,
    private val customerRepository: CustomerRepository,
    private val authenticationFacade: AuthenticationFacade
) {

    @Transactional
    fun createOrder(request: CreateOrderRequest): OrderDTO {
        val orderLines = getOrderLines(request.lines)
        updateStocks(orderLines)

        val customer = getCurrentUser()
        val order = getOrder(customer, request, orderLines)
        return orderRepository.save(order).toDTO()
    }

    private fun getOrderLines(lines: List<OrderLineRequest>): List<OrderLine> {
        return lines.map {
            val variantInfo = variantRepository.getVariantInfoById(it.variantId!!.toObjectId(), Language.En)
            OrderLine(id = ObjectId(), variantInfo = variantInfo, quantity = it.quantity)
        }
    }

    private fun updateStocks(lines: List<OrderLine>) {
        lines.forEach {
            val variantId = it.variantInfo.variants.id
            variantRepository.updateStock(variantId, -it.quantity)
        }
    }

    private fun getCurrentUser(): Customer {
        val customerId = authenticationFacade.getAuthentication().name.toObjectId()
        return customerRepository.getById(customerId)
    }

    private fun getOrder(customer: Customer, request: CreateOrderRequest, orderLines: List<OrderLine>): Order {
        return Order(
            id = ObjectId(),
            customerId = customer.id!!,
            customer = customer,
            deliveryService = request.deliveryService!!,
            city = request.city!!,
            department = request.department!!,
            recipientName = request.recipientName!!,
            recipientSurname = request.recipientSurname!!,
            recipientMiddleName = request.recipientMiddleName!!,
            lines = orderLines,
            status = OrderStatus.AWAITING_PAYMENT
        )
    }

    fun updateStatus(orderId: String, request: UpdateOrderStatusRequest): OrderDTO {
        val order = orderRepository.getById(orderId.toObjectId())
        order.updateStatus(request.status!!)
        return orderRepository.save(order).toDTO()
    }

    fun getOrderDTO(orderId: String): OrderDTO {
        return orderRepository.getById(orderId.toObjectId()).toDTO()
    }
}
