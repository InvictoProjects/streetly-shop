package com.invoctoprojects.streetlyshop.web.filter

import com.invoctoprojects.streetlyshop.service.JWTService
import com.invoctoprojects.streetlyshop.web.config.SecurityConfig
import io.jsonwebtoken.MalformedJwtException
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.io.PrintWriter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ExtendWith(MockitoExtension::class)
internal class JWTAuthenticationFilterTest {

    @Mock
    lateinit var jwtService: JWTService

    @Mock
    lateinit var request: HttpServletRequest

    @Mock
    lateinit var response: HttpServletResponse

    @Mock
    lateinit var filterChain: FilterChain

    @Mock
    lateinit var writer: PrintWriter

    @InjectMocks
    lateinit var jwtAuthenticationFilter: JWTAuthenticationFilter

    @AfterEach
    fun clearContext() {
        SecurityContextHolder.getContext().authentication = null
    }

    @Test
    fun doFilterInternal_tokenIsValid_authenticationSucceeds() {
        val token = "validToken"

        BDDMockito.given(request.getHeader(SecurityConfig.JWT_HEADER)).willReturn(token)

        val authentication = UsernamePasswordAuthenticationToken("john", "password")
        BDDMockito.given(jwtService.authenticate(token)).willReturn(authentication)

        val doFilterInternal = JWTAuthenticationFilter::class.java.getDeclaredMethod(
            "doFilterInternal",
            HttpServletRequest::class.java,
            HttpServletResponse::class.java,
            FilterChain::class.java
        )

        doFilterInternal.isAccessible = true
        doFilterInternal.invoke(jwtAuthenticationFilter, request, response, filterChain)

        AssertionsForInterfaceTypes.assertThat(SecurityContextHolder.getContext().authentication).isEqualTo(authentication)
    }

    @Test
    fun doFilterInternal_tokenIsNotPresent_authenticationIsNotHappening() {
        BDDMockito.given(request.getHeader(SecurityConfig.JWT_HEADER)).willReturn(null)

        val doFilterInternal = JWTAuthenticationFilter::class.java.getDeclaredMethod(
            "doFilterInternal",
            HttpServletRequest::class.java,
            HttpServletResponse::class.java,
            FilterChain::class.java
        )

        doFilterInternal.isAccessible = true
        doFilterInternal.invoke(jwtAuthenticationFilter, request, response, filterChain)

        AssertionsForInterfaceTypes.assertThat(SecurityContextHolder.getContext().authentication).isNull()
        BDDMockito.verifyNoInteractions(jwtService)
    }

    @Test
    fun doFilterInternal_tokenIsInValid_authenticationIsNotHappening() {
        val token = "invalidToken"

        BDDMockito.given(request.getHeader(SecurityConfig.JWT_HEADER)).willReturn(token)
        BDDMockito.given(jwtService.authenticate(token)).willThrow(MalformedJwtException("error"))
        BDDMockito.given(response.writer).willReturn(writer)

        val doFilterInternal = JWTAuthenticationFilter::class.java.getDeclaredMethod(
            "doFilterInternal",
            HttpServletRequest::class.java,
            HttpServletResponse::class.java,
            FilterChain::class.java
        )

        doFilterInternal.isAccessible = true
        doFilterInternal.invoke(jwtAuthenticationFilter, request, response, filterChain)

        AssertionsForInterfaceTypes.assertThat(SecurityContextHolder.getContext().authentication).isNull()
        BDDMockito.verifyNoInteractions(filterChain)
        Mockito.verify(response).status = HttpServletResponse.SC_FORBIDDEN
    }
}
