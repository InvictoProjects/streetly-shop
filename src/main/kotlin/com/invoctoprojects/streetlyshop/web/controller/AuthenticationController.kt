package com.invoctoprojects.streetlyshop.web.controller

import com.invoctoprojects.streetlyshop.service.LoginService
import com.invoctoprojects.streetlyshop.web.controller.request.BasicLoginRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.bson.types.ObjectId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Authentication Controller")
@Validated
@RestController
@RequestMapping("/v1/api/auth")
class AuthenticationController(val loginService: LoginService) {

    @ApiOperation("Login using email and password")
    @PostMapping("basic-login")
    fun basicLogin(@Valid @RequestBody loginRequest: BasicLoginRequest) = loginService.login(loginRequest)

    @ApiOperation("Get access token using refresh token")
    @PostMapping("refresh")
    fun refresh(@CookieValue(name = "refreshToken") refreshToken: String, @RequestParam userId: String) =
        loginService.refresh(ObjectId(userId), refreshToken)
}
