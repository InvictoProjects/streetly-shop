package com.invoctoprojects.streetlyshop.service

import com.invoctoprojects.streetlyshop.persistence.CustomerRepository
import com.invoctoprojects.streetlyshop.web.controller.request.BasicLoginRequest
import com.invoctoprojects.streetlyshop.web.controller.response.LoginResponse
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class LoginService(
    val customerRepository: CustomerRepository,
    val passwordEncoder: PasswordEncoder,
    val jwtService: JWTService
) {

    fun login(loginRequest: BasicLoginRequest): ResponseEntity<LoginResponse> {
        val user = customerRepository.getByEmail(loginRequest.email!!)

        if (!passwordEncoder.matches(loginRequest.password!!, user.password)) {
            throw BadCredentialsException("Invalid password was passed for email: ${loginRequest.email}")
        }

        val refreshTokenCookie = getRefreshTokenCookie(user.id!!)

        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Set-Cookie", refreshTokenCookie.toString())
            .body(LoginResponse(jwtService.generateAccessToken(user)))
    }

    private fun getRefreshTokenCookie(userId: ObjectId): ResponseCookie {
        val refreshToken = jwtService.getRefreshToken(userId)
        val maxAge = (refreshToken.expiration.time - Date().time) / 1000
        return ResponseCookie
            .from("refreshToken", refreshToken.token)
            .httpOnly(true)
            .path("/v1/api/auth/refresh")
            .maxAge(maxAge)
            .build()
    }

}
