package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.domain.model.order.OrderStatus
import com.invictoprojects.streetlyshop.service.OrderService
import com.invictoprojects.streetlyshop.service.ResourceReader
import com.invictoprojects.streetlyshop.web.controller.dto.OrderDTO
import com.invictoprojects.streetlyshop.web.controller.request.CreateOrderRequest
import com.invictoprojects.streetlyshop.web.controller.request.OrderLineRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateOrderStatusRequest
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
internal class OrderControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var orderService: OrderService

    @InjectMocks
    lateinit var controller: OrderController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun createOrder_requestIsValid_orderIsCreated() {
        val request = CreateOrderRequest(
            deliveryService = "Nova Poshta",
            city = "Kyiv",
            department = "N24",
            recipientName = "John",
            recipientSurname = "Black",
            recipientMiddleName = "Red",
            recipientPhone = "380991123456",
            lines = listOf(OrderLineRequest(variantId = ObjectId().toString(), quantity = 1))
        )

        val orderDTO = objectMapper.readValue(ResourceReader.readResource("model/orderDTO.json"), OrderDTO::class.java)
        given(orderService.createOrder(request)).willReturn(orderDTO)

        val actualResponse = mockMvc.perform(
            post("/v1/api/order")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(orderDTO))
    }

    @Test
    fun updateOrderStatus_requestIsValid_orderIsUpdated() {
        val orderId = ObjectId().toString()
        val request = UpdateOrderStatusRequest(status = OrderStatus.AWAITING_FULFILLMENT)

        val orderDTO = objectMapper.readValue(ResourceReader.readResource("model/orderDTO.json"), OrderDTO::class.java)
        given(orderService.updateStatus(orderId, request)).willReturn(orderDTO)

        val actualResponse = mockMvc.perform(
            put("/v1/api/order/$orderId")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(orderDTO))
    }

    @Test
    fun getOrder_orderExists_orderIsReturned() {
        val orderId = ObjectId().toString()

        val orderDTO = objectMapper.readValue(ResourceReader.readResource("model/orderDTO.json"), OrderDTO::class.java)
        given(orderService.getOrderDTO(orderId)).willReturn(orderDTO)

        val actualResponse = mockMvc.perform(
            get("/v1/api/order/$orderId")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(orderDTO))
    }
}
