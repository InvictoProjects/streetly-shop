package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.LoginService
import com.invictoprojects.streetlyshop.web.controller.request.BasicLoginRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.bson.types.ObjectId
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
