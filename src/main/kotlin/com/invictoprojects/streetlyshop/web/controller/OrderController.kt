package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.OrderService
import com.invictoprojects.streetlyshop.web.controller.dto.OrderDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateOrderRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateOrderStatusRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Api("Order Controller")
@Validated
@RestController
@RequestMapping("/v1/api/order")
class OrderController(
    val orderService: OrderService
) {

    @ApiOperation("Create order")
    @PostMapping
    fun createOrder(
        @Valid
        @RequestBody
        request: CreateOrderRequest
    ): OrderDTO = orderService.createOrder(request)

    @ApiOperation("Update order status")
    @PutMapping("{id}")
    fun updateOrderStatus(@PathVariable id: String, @Valid @RequestBody request: UpdateOrderStatusRequest): OrderDTO =
        orderService.updateStatus(id, request)

    @ApiOperation("Get order by id")
    @GetMapping("{id}")
    fun getOrder(@PathVariable id: String): OrderDTO = orderService.getOrderDTO(id)
}
