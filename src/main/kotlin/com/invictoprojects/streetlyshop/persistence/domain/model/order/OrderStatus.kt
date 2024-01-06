package com.invictoprojects.streetlyshop.persistence.domain.model.order

enum class OrderStatus {
    PENDING, AWAITING_PAYMENT, AWAITING_FULFILLMENT, AWAITING_SHIPMENT, AWAITING_PICKUP, COMPLETED, CANCELLED, REFUNDED
}
