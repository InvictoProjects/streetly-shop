package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.CustomerService
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.request.AddFavoriteProductRequest
import com.invictoprojects.streetlyshop.web.controller.request.RegisterRequest
import com.invictoprojects.streetlyshop.web.controller.request.RemoveFavoriteProductRequest
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCustomerDetailsRequest
import com.invictoprojects.streetlyshop.web.controller.response.ImageUploadResponse
import com.invictoprojects.streetlyshop.web.validator.ValidImage
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.bson.types.ObjectId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
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

    @ApiOperation("Upload avatar photo")
    @PostMapping("avatar")
    fun updateAvatar(@ValidImage @RequestPart("file") file: MultipartFile): ImageUploadResponse {
        val userId = authenticationFacade.getAuthentication().name
        return customerService.updateAvatar(file, ObjectId(userId))
    }

    @ApiOperation("Update customer details")
    @PutMapping
    fun updateDetails(@Valid @RequestBody updateRequest: UpdateCustomerDetailsRequest) {
        val userId = authenticationFacade.getAuthentication().name
        customerService.updateDetails(ObjectId(userId), updateRequest)
    }

    @ApiOperation("Add favorite product")
    @PostMapping("favorite-product")
    fun addFavoriteProduct(@Valid @RequestBody request: AddFavoriteProductRequest) {
        val userId = authenticationFacade.getAuthentication().name
        customerService.addFavoriteProduct(userId, request.productId!!)
    }

    @ApiOperation("Remove favorite product")
    @DeleteMapping("favorite-product")
    fun removeFavoriteProduct(@Valid @RequestBody request: RemoveFavoriteProductRequest) {
        val userId = authenticationFacade.getAuthentication().name
        customerService.removeFavoriteProduct(userId, request.productId!!)
    }

}
