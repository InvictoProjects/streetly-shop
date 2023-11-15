package com.invoctoprojects.streetlyshop.web.config

import com.invoctoprojects.streetlyshop.persistence.domain.customer.Role
import com.invoctoprojects.streetlyshop.web.filter.JWTAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import javax.servlet.http.HttpServletRequest

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationFilter: JWTAuthenticationFilter): SecurityFilterChain {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().cors()
            .and().csrf().disable()
            .authorizeHttpRequests()

            .antMatchers("/v1/api/auth/**").permitAll()

            .antMatchers("/").permitAll()

            .antMatchers(
                "/swagger-resources/**",
                "/v3/api-docs",
                "/swagger-ui/**",
                "/webjars/**",
            ).permitAll()

            .anyRequest().authenticated()
            .and().addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter::class.java)
            .httpBasic().disable()
            .formLogin().disable()

        return http.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        return DefaultCorsConfigurationSource()
    }

    companion object {
        const val JWT_HEADER = "Authorization"
    }
}

class DefaultCorsConfigurationSource : CorsConfigurationSource {
    override fun getCorsConfiguration(request: HttpServletRequest): CorsConfiguration {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("*")
        config.allowedMethods = listOf("*")
        config.allowedHeaders = listOf("*")
        config.exposedHeaders = listOf("*")
        config.maxAge = 3600

        return config
    }

}
