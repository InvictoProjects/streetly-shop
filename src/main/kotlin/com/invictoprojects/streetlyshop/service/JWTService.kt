package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.RefreshTokenRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.RefreshToken
import com.invictoprojects.streetlyshop.web.exception.InvalidRefreshTokenException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.apache.commons.lang3.RandomStringUtils
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.*

@Service
class JWTService(
    @Value("\${jwt.secret}") val jwtKey: String,
    @Value("\${jwt.access.expiration}") val accessTokenExpirationTime: Long,
    @Value("\${jwt.refresh.expiration}") val refreshTokenExpirationTime: Long,
    val refreshTokenRepository: RefreshTokenRepository
) {

    fun generateAccessToken(user: Customer): String {
        val key = Keys.hmacShaKeyFor(jwtKey.toByteArray())
        val currentTime = Date().time
        return Jwts.builder()
            .setIssuer("Streetly-shop")
            .setSubject("JWT Token")
            .claim("userId", user.id!!.toString())
            .claim("roles", user.roles.joinToString { it.role })
            .setIssuedAt(Date(currentTime))
            .setExpiration(Date(currentTime + accessTokenExpirationTime))
            .signWith(key)
            .compact()
    }

    fun createRefreshToken(userId: ObjectId): RefreshToken {
        val expiration = Date(Date().time + refreshTokenExpirationTime)
        val refreshToken = RefreshToken(userId, generateSecret(REFRESH_TOKEN_LENGTH), expiration)
        refreshTokenRepository.save(refreshToken)

        return refreshToken
    }

    fun getRefreshToken(userId: ObjectId): RefreshToken {
        return refreshTokenRepository.findById(userId)
            .filter { !it.isExpired() }
            .orElseGet { createRefreshToken(userId) }
    }

    fun authenticate(jwtToken: String): Authentication {
        val key = Keys.hmacShaKeyFor(jwtKey.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwtToken)
            .body

        val userId = claims["userId"] as String
        val rolesString = claims["roles"] as String
        val roles = rolesString.split(",").map { SimpleGrantedAuthority(it.trim()) }
        return UsernamePasswordAuthenticationToken(userId, null, roles)
    }

    fun validateRefreshToken(userId: ObjectId, refreshToken: String) {
        refreshTokenRepository.findById(userId)
            .filter { !it.isExpired() }
            .filter { it.token == refreshToken }
            .orElseThrow { InvalidRefreshTokenException(userId) }
    }

    private fun generateSecret(length: Int) = RandomStringUtils.randomAlphanumeric(length)

    companion object {
        const val REFRESH_TOKEN_LENGTH = 40
    }
}
