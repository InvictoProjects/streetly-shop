package com.invictoprojects.streetlyshop.service.facade

import com.invictoprojects.streetlyshop.web.exception.AuthenticationRequiredException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

@ExtendWith(MockitoExtension::class)
internal class DefaultAuthenticationFacadeTest {

    @InjectMocks
    lateinit var authenticationFacade: DefaultAuthenticationFacade

    @BeforeEach
    fun clearContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun getAuthentication_userIsNotAuthenticated_exceptionIsThrown() {
        SecurityContextHolder.getContext().authentication = null

        val throwable = catchThrowable { authenticationFacade.getAuthentication() }

        assertThat(throwable).isInstanceOf(AuthenticationRequiredException::class.java)
    }

    @Test
    fun getAuthentication_userIsAuthenticated_authenticationIsReturned() {
        val authentication = UsernamePasswordAuthenticationToken("userId", "password")
        SecurityContextHolder.getContext().authentication = authentication

        val auth = authenticationFacade.getAuthentication()

        assertThat(auth).isEqualTo(authentication)
    }
}
