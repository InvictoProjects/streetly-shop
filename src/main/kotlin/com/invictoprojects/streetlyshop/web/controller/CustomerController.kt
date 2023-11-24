package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.CustomerService
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.request.RegisterRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCustomerDetailsRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.bson.types.ObjectId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Customer Controller")
@Validated
@RestController
@RequestMapping("/v1/api/customer")
class CustomerController(
    val customerService: CustomerService,
    val authenticationFacade: AuthenticationFacade
) {

    @ApiOperation("Register customer using email and password")
    @PostMapping("register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest) {
        customerService.register(registerRequest.email!!, registerRequest.password!!)
    }

    @ApiOperation("Update customer details")
    @PutMapping
    fun updateDetails(@Valid @RequestBody updateRequest: UpdateCustomerDetailsRequest) {
        val userId = authenticationFacade.getAuthentication().name
        customerService.updateDetails(ObjectId(userId), updateRequest)
    }

}
