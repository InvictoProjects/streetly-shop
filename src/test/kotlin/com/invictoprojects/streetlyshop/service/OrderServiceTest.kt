package com.invictoprojects.streetlyshop.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.persistence.*
import com.invictoprojects.streetlyshop.persistence.domain.model.*
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.order.Order
import com.invictoprojects.streetlyshop.persistence.domain.model.order.OrderStatus
import com.invictoprojects.streetlyshop.persistence.domain.model.product.Product
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.Variant
import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.VariantInfo
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.request.CreateOrderRequest
import com.invictoprojects.streetlyshop.web.controller.request.OrderLineRequest
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.*


@ExtendWith(MockitoExtension::class)
internal class OrderServiceTest {

    @Mock
    lateinit var orderRepository: com.invictoprojects.streetlyshop.persistence.OrderRepository

    @Mock
    lateinit var variantRepository: com.invictoprojects.streetlyshop.persistence.VariantRepository

    @Mock
    lateinit var customerRepository: com.invictoprojects.streetlyshop.persistence.CustomerRepository

    @Mock
    lateinit var authenticationFacade: AuthenticationFacade

    @InjectMocks
    lateinit var orderService: OrderService

    @Captor
    lateinit var orderCaptor: ArgumentCaptor<Order>

    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun createOrder_requestIsValid_orderIsCreated() {
        // given
        val variantId = ObjectId("64767ffead10a76bbfb85b66")
        val request = CreateOrderRequest(
                deliveryService = "Nova Poshta",
                city = "Kyiv",
                department = "N24",
                recipientName = "John",
                recipientSurname = "Black",
                recipientMiddleName = "Red",
                recipientPhone = "380991123456",
                lines = listOf(OrderLineRequest(variantId = variantId.toString(), quantity = 1))
        )

        val variantInfo = VariantInfo(
                contentId = ObjectId(),
                productId = ObjectId(),
                name = "White T-Shirt",
                description = "Cool T-Shirt",
                createdBy = ObjectId(),
                languageCode = Language.Ua,
                product = Product(id = ObjectId(), categoryId = ObjectId(), createdBy = ObjectId()),
                variants = Variant(
                        id = variantId,
                        barcode = "123",
                        productId = ObjectId(),
                        contentId = ObjectId(),
                        createdBy = ObjectId()
                )
        )
        given(variantRepository.getVariantInfoById(variantId, Language.En)).willReturn(variantInfo)

        val currentUserId = ObjectId()
        val authentication = UsernamePasswordAuthenticationToken(currentUserId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        val customer = Customer(id = currentUserId, nickname = "John", email = "john@gmail.com", password = "encoded")
        given(customerRepository.getById(currentUserId)).willReturn(customer)

        given(orderRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Order>())

        // when
        val orderDTO = orderService.createOrder(request)

        // then
        verify(variantRepository).updateStock(variantId, -1)
        verify(orderRepository).save(capture(orderCaptor))

        val actualOrder = orderCaptor.value
        assertThat(actualOrder.status).isEqualTo(OrderStatus.AWAITING_PAYMENT)

        assertThat(orderDTO).isEqualTo(actualOrder.toDTO())
    }

    private fun <T> any(): T = Mockito.any()
    private fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
}
