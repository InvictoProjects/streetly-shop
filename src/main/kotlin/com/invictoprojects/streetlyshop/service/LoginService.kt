package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.CustomerRepository
import com.invictoprojects.streetlyshop.web.controller.request.BasicLoginRequest
import com.invictoprojects.streetlyshop.web.controller.response.LoginResponse
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

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
        val maxAge = TimeUnit.MILLISECONDS.toSeconds((refreshToken.expiration.time - Date().time))
        return ResponseCookie
            .from("refreshToken", refreshToken.token)
            .httpOnly(true)
            .path("/v1/api/auth/refresh")
            .maxAge(maxAge)
            .build()
    }

    fun refresh(userId: ObjectId, refreshToken: String): LoginResponse {
        jwtService.validateRefreshToken(userId, refreshToken)
        val user = customerRepository.getById(userId)
        return LoginResponse(jwtService.generateAccessToken(user))
    }
}
