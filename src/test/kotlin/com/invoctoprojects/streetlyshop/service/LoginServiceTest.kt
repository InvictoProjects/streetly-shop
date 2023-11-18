package com.invoctoprojects.streetlyshop.service

import com.invoctoprojects.streetlyshop.persistence.CustomerRepository
import com.invoctoprojects.streetlyshop.persistence.domain.customer.Customer
import com.invoctoprojects.streetlyshop.persistence.domain.customer.RefreshToken
import com.invoctoprojects.streetlyshop.persistence.domain.customer.Role
import com.invoctoprojects.streetlyshop.web.controller.request.BasicLoginRequest
import com.invoctoprojects.streetlyshop.web.controller.response.LoginResponse
import com.invoctoprojects.streetlyshop.web.exception.InvalidRefreshTokenException
import com.invoctoprojects.streetlyshop.web.exception.UserNotFoundException
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class LoginServiceTest {

    @Mock
    lateinit var customerRepository: CustomerRepository

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Mock
    lateinit var jwtService: JWTService

    @InjectMocks
    lateinit var loginService: LoginService

    @Test
    fun login_emailIsNotFound_usernameNotFoundExceptionIsThrown() {
        BDDMockito.given(customerRepository.getByEmail("email"))
            .willThrow(UserNotFoundException("not found"))

        val throwable = Assertions.catchThrowable { loginService.login(BasicLoginRequest("email", "password")) }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun login_passwordIsInvalid_badCredentialsExceptionIsThrown() {
        val email = "john@gmail.com"
        val user = Customer(ObjectId(), password = "pass", email = email, nickname = email, roles = listOf(Role.BUYER))

        BDDMockito.given(customerRepository.getByEmail(email)).willReturn(user)
        BDDMockito.given(passwordEncoder.matches("password", "pass")).willReturn(false)

        val throwable = Assertions.catchThrowable { loginService.login(BasicLoginRequest(email, "password")) }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(BadCredentialsException::class.java)
    }

    @Test
    fun login_passwordIsValid_responseIsReturned() {
        val email = "john@gmail.com"
        val userId = ObjectId()
        val user = Customer(userId, password = "password", email = email, nickname = email, roles = listOf(Role.BUYER))

        BDDMockito.given(customerRepository.getByEmail(email)).willReturn(user)
        BDDMockito.given(passwordEncoder.matches("password", "password")).willReturn(true)
        BDDMockito.given(jwtService.generateAccessToken(user)).willReturn("token")

        val currentTime = Date().time
        val refreshToken = RefreshToken(userId, "refreshToken", Date(currentTime + 60000))
        BDDMockito.given(jwtService.getRefreshToken(userId)).willReturn(refreshToken)

        val response = loginService.login(BasicLoginRequest(email, "password"))

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isEqualTo(LoginResponse("token"))
        assertThat(response.headers["Set-Cookie"]).hasSize(1)
        assertThat(response.headers["Set-Cookie"]!![0]).contains("refreshToken=refreshToken")
    }

    @Test
    fun refresh_tokenIsInvalid_exceptionIsThrown() {
        val userId = ObjectId()
        val refreshToken = "refreshToken"

        BDDMockito.given(jwtService.validateRefreshToken(userId, refreshToken)).willThrow(InvalidRefreshTokenException::class.java)

        val throwable = Assertions.catchThrowable { loginService.refresh(userId, refreshToken) }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(InvalidRefreshTokenException::class.java)
    }

    @Test
    fun refresh_tokenIsValid_loginResponseIsReturned() {
        val userId = ObjectId()
        val refreshToken = "refreshToken"

        val user = Customer(userId, password = "pass", email = "email", nickname = "email", roles = listOf(Role.BUYER))
        BDDMockito.given(customerRepository.getById(userId)).willReturn(user)
        BDDMockito.given(jwtService.generateAccessToken(user)).willReturn("access_token")

        val response = loginService.refresh(userId, refreshToken)

        assertThat(response).isEqualTo(LoginResponse("access_token"))
    }
}
