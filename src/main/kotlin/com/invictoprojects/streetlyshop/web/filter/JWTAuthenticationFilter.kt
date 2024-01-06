package com.invictoprojects.streetlyshop.web.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.service.JWTService
import com.invictoprojects.streetlyshop.web.config.SecurityConfig
import com.invictoprojects.streetlyshop.web.controller.response.ErrorResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JWTAuthenticationFilter(private val jwtService: JWTService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(SecurityConfig.JWT_HEADER)
        token?.let {
            try {
                val authentication = jwtService.authenticate(token)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (jwtException: RuntimeException) {
                handleInvalidToken(jwtException, response)
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun handleInvalidToken(jwtException: RuntimeException, response: HttpServletResponse) {
        val errorResponse = ErrorResponse("Token is invalid", mutableListOf(jwtException.toString()))

        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.writer.write(jacksonObjectMapper().writeValueAsString(errorResponse))
    }
}
