package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.RefreshTokenRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.RefreshToken
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Role
import com.invictoprojects.streetlyshop.web.exception.InvalidRefreshTokenException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.apache.commons.lang3.RandomStringUtils
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class JWTServiceTest {
    private var jwtKey: String = RandomStringUtils.randomAlphanumeric(40)
    private var accessTokenExpirationTime = 60000L
    private var refreshTokenExpirationTime = 60000L

    @Mock
    lateinit var refreshTokenRepository: RefreshTokenRepository

    private lateinit var jwtService: JWTService

    @BeforeEach
    fun setup() {
        jwtService = JWTService(jwtKey, accessTokenExpirationTime, refreshTokenExpirationTime, refreshTokenRepository)
    }

    @Test
    fun generateAccessToken_userIsValid_tokenIsReturned() {
        val roles = mutableListOf(Role.BUYER, Role.ADMIN)
        val email = "john@gmail.com"
        val user = Customer(ObjectId(), nickname = email, email = email, password = "password", roles = roles)
        val token = jwtService.generateAccessToken(user)

        val key = Keys.hmacShaKeyFor(jwtKey.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        val userId = claims["userId"] as String
        val rolesString = claims["roles"] as String

        AssertionsForInterfaceTypes.assertThat(userId).isEqualTo(user.id!!.toString())
        AssertionsForInterfaceTypes.assertThat(rolesString).isEqualTo("${Role.BUYER.role}, ${Role.ADMIN.role}")
    }

    @Test
    fun createRefreshToken_userIdIsValid_tokenIsReturned() {
        val userId = ObjectId()

        val refreshToken = jwtService.createRefreshToken(userId)

        assertThat(refreshToken.id).isEqualTo(userId)
        BDDMockito.verify(refreshTokenRepository).save(refreshToken)
    }

    @Test
    fun getRefreshToken_tokenIsNotFound_tokenIsCreated() {
        val userId = ObjectId()
        BDDMockito.given(refreshTokenRepository.findById(userId)).willReturn(Optional.empty())

        val refreshToken = jwtService.getRefreshToken(userId)

        assertThat(refreshToken.id).isEqualTo(userId)
        BDDMockito.verify(refreshTokenRepository).save(refreshToken)
    }

    @Test
    fun getRefreshToken_tokenIsExpired_tokenIsCreated() {
        val userId = ObjectId()

        val existingToken = RefreshToken(userId, "token", Date(Date().time - 60000))
        BDDMockito.given(refreshTokenRepository.findById(userId)).willReturn(Optional.of(existingToken))

        val refreshToken = jwtService.getRefreshToken(userId)

        assertThat(refreshToken.id).isEqualTo(userId)
        BDDMockito.verify(refreshTokenRepository).save(refreshToken)
    }

    @Test
    fun getRefreshToken_validTokenExists_tokenIsReturned() {
        val userId = ObjectId()

        val existingToken = RefreshToken(userId, "token", Date(Date().time + 60000))
        BDDMockito.given(refreshTokenRepository.findById(userId)).willReturn(Optional.of(existingToken))

        val refreshToken = jwtService.getRefreshToken(userId)

        assertThat(refreshToken).isEqualTo(existingToken)
        BDDMockito.verify(refreshTokenRepository, Mockito.never()).save(refreshToken)
    }

    @Test
    fun authenticate_tokenIsExpired_exceptionIsThrown() {
        val userId = "userId"
        val currentTime = Date().time
        val key = Keys.hmacShaKeyFor(jwtKey.toByteArray())

        val token = Jwts.builder()
            .setIssuer("Streetly-shop")
            .setSubject("JWT Token")
            .claim("userId", userId)
            .claim("roles", "${Role.BUYER.role}, ${Role.ADMIN.role}")
            .setIssuedAt(Date(currentTime - 60000))
            .setExpiration(Date(currentTime - 20000))
            .signWith(key)
            .compact()

        val throwable = Assertions.catchThrowable { jwtService.authenticate(token) }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(ExpiredJwtException::class.java)
    }

    @Test
    fun authenticate_tokenSignatureIsInvalid_exceptionIsThrown() {
        val userId = "userId"
        val currentTime = Date().time
        val otherKey = jwtKey + "1"
        val key = Keys.hmacShaKeyFor(otherKey.toByteArray())

        val token = Jwts.builder()
            .setIssuer("Streetly-shop")
            .setSubject("JWT Token")
            .claim("userId", userId)
            .claim("roles", "${Role.BUYER.role}, ${Role.ADMIN.role}")
            .setIssuedAt(Date(currentTime - 60000))
            .setExpiration(Date(currentTime + 60000))
            .signWith(key)
            .compact()

        val throwable = Assertions.catchThrowable { jwtService.authenticate(token) }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(SignatureException::class.java)
    }

    @Test
    fun authenticate_tokenIsMalformed_exceptionIsThrown() {
        val token = "malformed"

        val throwable = Assertions.catchThrowable { jwtService.authenticate(token) }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(MalformedJwtException::class.java)
    }

    @Test
    fun authenticate_tokenIsValid_authenticationIsReturned() {
        val userId = "userId"
        val currentTime = Date().time
        val key = Keys.hmacShaKeyFor(jwtKey.toByteArray())

        val token = Jwts.builder()
            .setIssuer("Streetly-shop")
            .setSubject("JWT Token")
            .claim("userId", userId)
            .claim("roles", "${Role.BUYER.role}, ${Role.ADMIN.role}")
            .setIssuedAt(Date(currentTime - 60000))
            .setExpiration(Date(currentTime + 60000))
            .signWith(key)
            .compact()

        val authentication = jwtService.authenticate(token)

        assertThat(authentication.name).isEqualTo(userId)
        assertThat(authentication.authorities).contains(SimpleGrantedAuthority(Role.BUYER.role))
        assertThat(authentication.authorities).contains(SimpleGrantedAuthority(Role.ADMIN.role))
    }

    @Test
    fun validateRefreshToken_tokenIsNotFound_exceptionIsThrown() {
        val userId = ObjectId()
        BDDMockito.given(refreshTokenRepository.findById(userId)).willReturn(Optional.empty())

        val throwable = Assertions.catchThrowable { jwtService.validateRefreshToken(userId, "token") }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(InvalidRefreshTokenException::class.java)
    }

    @Test
    fun validateRefreshToken_tokenIsExpired_exceptionIsThrown() {
        val userId = ObjectId()
        val currentTime = Date().time

        BDDMockito.given(refreshTokenRepository.findById(userId))
            .willReturn(Optional.of(RefreshToken(userId, "token", Date(currentTime - 60000))))

        val throwable = Assertions.catchThrowable { jwtService.validateRefreshToken(userId, "expired_token") }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(InvalidRefreshTokenException::class.java)
    }

    @Test
    fun validateRefreshToken_tokenIsInvalid_exceptionIsThrown() {
        val userId = ObjectId()
        val currentTime = Date().time

        BDDMockito.given(refreshTokenRepository.findById(userId))
            .willReturn(Optional.of(RefreshToken(userId, "token", Date(currentTime + 60000))))

        val throwable = Assertions.catchThrowable { jwtService.validateRefreshToken(userId, "invalid_token") }

        AssertionsForInterfaceTypes.assertThat(throwable).isInstanceOf(InvalidRefreshTokenException::class.java)
    }

    @Test
    fun validateRefreshToken_tokenIsValid_exceptionIsNotThrown() {
        val userId = ObjectId()
        val currentTime = Date().time

        BDDMockito.given(refreshTokenRepository.findById(userId))
            .willReturn(Optional.of(RefreshToken(userId, "token", Date(currentTime + 60000))))

        jwtService.validateRefreshToken(userId, "token")
    }
}
